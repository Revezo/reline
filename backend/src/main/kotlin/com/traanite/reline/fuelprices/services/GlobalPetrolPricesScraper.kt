package com.traanite.reline.fuelprices.services

import com.traanite.reline.fuelprices.model.Country
import com.traanite.reline.fuelprices.model.CountryFuelPriceData
import com.traanite.reline.fuelprices.model.FuelPrice
import com.traanite.reline.fuelprices.model.FuelType
import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterConfig
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.math.BigDecimal
import java.time.Duration
import java.util.*
import java.util.regex.Pattern

@Service
class GlobalPetrolPricesScraper {
    companion object {
        val log: Logger = LoggerFactory.getLogger(GlobalPetrolPricesScraper::class.java)
    }

    private val currency = Currency.getInstance("EUR")
    private val refreshPeriodSeconds: Long = 1
    private val limitForPeriod = 5
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
                        fuelPricesOfCountry.firstOrNull { it.fuelType == FuelType.Gasoline } ?: FuelPrice(FuelType.Gasoline, currency),
                        fuelPricesOfCountry.firstOrNull { it.fuelType == FuelType.Diesel } ?: FuelPrice(FuelType.Diesel, currency))
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
            .body(Mono.just("literGalon=1&currency=${currency.currencyCode}"), String::class.java)
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
        val tableFuelPrice = doc.select("#graphic > table:nth-child(1) > tbody:nth-child(2) > tr:nth-child(1) > td:nth-child(2)")
        val headerWithCountryName = doc.select("#graphPageLeft > h1:nth-child(1)")

        if (!tableFuelPrice.isEmpty()) {
            val fuelPriceValue = tableFuelPrice.text()
            log.debug("fuelPriceValue: $fuelPriceValue")
            log.debug("headerWithCountryName: ${headerWithCountryName.text()}")
            val p =
                Pattern.compile("(.*) (Diesel|Gasoline) prices, (.*)")
            val m = p.matcher(headerWithCountryName.text())

            if (m.find()) {
                val countryValue = m.group(1)
                val country = Country(countryValue)
                return (country to FuelPrice(
                    fuelType,
                    BigDecimal(fuelPriceValue.replace(",", "")),
                    currency))
                    .toMono()
            }
        }

        val newsHeadlines = doc.select("div.tipInfo > div:nth-child(1)")
        val text = newsHeadlines.text()
        log.debug(text)

        val p1 = Pattern.compile("(.*): The price of (.*) is (.*) ${currency.displayName} per (litre|liter). (.*)")
        val m1 = p1.matcher(text)
        if (m1.find()) {
            val countryValue = m1.group(1)
            val fuelPriceValue = m1.group(3)
            log.debug("fuelPriceValue: $fuelPriceValue")
            log.debug("Country: $countryValue")
            val country = Country(countryValue)
            return (country to FuelPrice(
                fuelType,
                BigDecimal(fuelPriceValue),
                currency))
                .toMono()
        }
        return Mono.empty()
    }
}