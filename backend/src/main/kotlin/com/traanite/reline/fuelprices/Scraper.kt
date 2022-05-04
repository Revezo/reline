package com.traanite.reline.fuelprices

import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterConfig
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.math.BigDecimal
import java.time.Duration
import java.util.*
import java.util.regex.Pattern

class Scraper {

    private val webClient = WebClient.builder()
        .defaultCookie("my_session_id", UUID.randomUUID().toString())
        .baseUrl("https://www.globalpetrolprices.com/")
        .build()

    private val rateLimiter = RateLimiter.of(
        "my-rate-limiter",
        RateLimiterConfig.custom()
            .limitRefreshPeriod(Duration.ofSeconds(1))
            .limitForPeriod(20)
            .timeoutDuration(Duration.ofMinutes(5))
            .build()
    )

    fun scrape(fuelType: FuelType, petrolUri: String): Flux<CountryFuelPriceData> {
        return scrapeUriPage(petrolUri)
            .flatMapIterable {processUris(it)}
            .map { it.attr("href") }
            .flatMap { changeUnits(it) }
            .flatMap { scrapeValuePage(it) }
            .flatMap { processValuePage(it, fuelType) }
    }

    private fun scrapeUriPage(petrolUri: String): Mono<String> {
        return webClient.get()
            .uri(petrolUri)
            .retrieve()
            .bodyToMono(String::class.java)
            .transformDeferred(RateLimiterOperator.of(rateLimiter))
    }

    private fun processUris(pageHtml: String): Elements {
        val doc = Jsoup.parseBodyFragment(pageHtml)
        return doc.select(".graph_outside_link")
    }

    private fun changeUnits(countryPageUri: String): Mono<String> {
        return webClient.post()
            .uri(countryPageUri)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .body(Mono.just("literGalon=1&currency=EUR"), String::class.java)
            .retrieve()
            .toBodilessEntity()
            .transformDeferred(RateLimiterOperator.of(rateLimiter))
            .map { countryPageUri }
    }

    private fun scrapeValuePage(uri: String): Mono<String> {
        return webClient.get()
            .uri(uri)
            .retrieve()
            .bodyToMono(String::class.java)
            .transformDeferred(RateLimiterOperator.of(rateLimiter))
    }

    private fun processValuePage(pageHtml: String, fuelType: FuelType): Mono<CountryFuelPriceData> {
        val doc = Jsoup.parseBodyFragment(pageHtml)
        val newsHeadlines = doc.select("div.tipInfo > div:nth-child(1)")
        val text = newsHeadlines.text()
        println(text)

        val p =
            Pattern.compile("(.*)The average value for (.*) during that period was (.*) Euro with a minimum of (.*) Euro on (.*) and a maximum of (.*) Euro on (.*).")
        val m = p.matcher(text)
        if (m.find()) {
            println("Country: ${m.group(2)}")
            println("Avg price: ${m.group(3)}")
            println("Min price: ${m.group(4)}")
            println("Max price: ${m.group(6)}")

            val country = Country(m.group(2))
            return CountryFuelPriceData(
                country, ComplexFuelPrice(
                    fuelType,
                    BigDecimal(m.group(3)),
                    BigDecimal(m.group(4)),
                    BigDecimal(m.group(6))
                )
            ).toMono()
        } else {
            val p1 = Pattern.compile("(.*): The price of (.*) is (.*) Euro per (litre|liter). (.*)")
            val m1 = p1.matcher(text)
            if (m1.find()) {
                println("Country: ${m1.group(1)}")
                println("Avg price: ${m1.group(3)}")
                val country = Country(m1.group(1))
                return CountryFuelPriceData(
                    country,
                    SimpleFuelPrice(fuelType, BigDecimal(m1.group(3)))
                ).toMono()
            }
        }
        return Mono.empty()
    }
}