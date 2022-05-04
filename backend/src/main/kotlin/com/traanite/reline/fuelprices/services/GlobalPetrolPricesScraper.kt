package com.traanite.reline.fuelprices.services

import com.traanite.reline.fuelprices.model.*
import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterConfig
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.math.BigDecimal
import java.time.Duration
import java.util.*
import java.util.regex.Pattern

class GlobalPetrolPricesScraper {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    private val refreshPeriodSeconds: Long = 1
    private val limitForPeriod = 20
    private val timeoutDurationMinutes: Long = 5

    private val baseUri = "https://www.globalpetrolprices.com/"
    private val dieselUri = "/diesel_prices/"
    private val gasolineUri = "/gasoline_prices/"

    private val rateLimiter = RateLimiter.of(
        "global-petrol-prices-rate-limiter",
        RateLimiterConfig.custom()
            .limitRefreshPeriod(Duration.ofSeconds(refreshPeriodSeconds))
            .limitForPeriod(limitForPeriod)
            .timeoutDuration(Duration.ofMinutes(timeoutDurationMinutes))
            .build()
    )

    fun getPrices(): Flux<CountryFuelPriceData> {
        val webClient = newWebClient()

        val gasolinePrices = scrapeDataForFuelType(FuelType.Gasoline, webClient)
        val dieselPrices = scrapeDataForFuelType(FuelType.Diesel, webClient)

        return Flux.concat(gasolinePrices, dieselPrices)
            .groupBy({ it.first }, { it.second })
            .flatMap { idFlux ->
                idFlux.collectList().map { fuelPricesOfCountry ->
                    CountryFuelPriceData(
                        idFlux.key(),
                        fuelPricesOfCountry.firstOrNull { it.fuelType() == FuelType.Gasoline } ?: EmptyFuelPrice(FuelType.Gasoline),
                        fuelPricesOfCountry.firstOrNull { it.fuelType() == FuelType.Diesel } ?: EmptyFuelPrice(FuelType.Diesel))
                }
            }
    }

    private fun scrapeDataForFuelType(fuelType: FuelType, webClient: WebClient): Flux<Pair<Country, FuelPrice>> {
        val pageUri = when (fuelType) {
            FuelType.Diesel -> dieselUri
            FuelType.Gasoline -> gasolineUri
        }

        return scrapeUriPage(pageUri, webClient)
            .flatMapIterable { processUris(it) }
            .map { it.attr("href") }
            .flatMap { changeUnits(it, webClient) }
            .flatMap { scrapeValuePage(it, webClient) }
            .flatMap { processValuePage(it, fuelType) }
    }

    private fun newWebClient(): WebClient {
        return WebClient.builder()
            .defaultCookie("my_session_id", UUID.randomUUID().toString())
            .baseUrl(baseUri)
            .build()
    }

    private fun scrapeUriPage(petrolUri: String, webClient: WebClient): Mono<String> {
        return webClient.get()
            .uri(petrolUri)
            .retrieve()
            .bodyToMono(String::class.java)
            .transformDeferred(RateLimiterOperator.of(this.rateLimiter))
    }

    private fun processUris(pageHtml: String): Elements {
        val doc = Jsoup.parseBodyFragment(pageHtml)
        return doc.select(".graph_outside_link")
    }

    private fun changeUnits(countryPageUri: String, webClient: WebClient): Mono<String> {
        return webClient.post()
            .uri(countryPageUri)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .body(Mono.just("literGalon=1&currency=EUR"), String::class.java)
            .retrieve()
            .toBodilessEntity()
            .transformDeferred(RateLimiterOperator.of(this.rateLimiter))
            .map { countryPageUri }
    }

    private fun scrapeValuePage(uri: String, webClient: WebClient): Mono<String> {
        return webClient.get()
            .uri(uri)
            .retrieve()
            .bodyToMono(String::class.java)
            .transformDeferred(RateLimiterOperator.of(this.rateLimiter))
    }

    private fun processValuePage(pageHtml: String, fuelType: FuelType): Mono<Pair<Country, FuelPrice>> {
        val doc = Jsoup.parseBodyFragment(pageHtml)
        val newsHeadlines = doc.select("div.tipInfo > div:nth-child(1)")
        val text = newsHeadlines.text()
        log.debug(text)

        val p =
            Pattern.compile("(.*)The average value for (.*) during that period was (.*) Euro with a minimum of (.*) Euro on (.*) and a maximum of (.*) Euro on (.*).")
        val m = p.matcher(text)
        if (m.find()) {
            log.debug("Country: ${m.group(2)}")
            log.debug("Avg price: ${m.group(3)}")
            log.debug("Min price: ${m.group(4)}")
            log.debug("Max price: ${m.group(6)}")

            val country = Country(m.group(2))
            return (country to ComplexFuelPrice(
                fuelType,
                BigDecimal(m.group(3)),
                BigDecimal(m.group(4)),
                BigDecimal(m.group(6)), Currency.getInstance("EUR")
            )).toMono()
        } else {
            val p1 = Pattern.compile("(.*): The price of (.*) is (.*) Euro per (litre|liter). (.*)")
            val m1 = p1.matcher(text)
            if (m1.find()) {
                log.debug("Country: ${m1.group(1)}")
                log.debug("Avg price: ${m1.group(3)}")
                val country = Country(m1.group(1))
                return (country to SimpleFuelPrice(fuelType, BigDecimal(m1.group(3)), Currency.getInstance("EUR"))).toMono()
            }
        }
        return Mono.empty()
    }
}