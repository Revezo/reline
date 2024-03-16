package com.traanite.reline.currency

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.math.BigDecimal

private val logger = KotlinLogging.logger {}

@Service
class CurrencyExchangeApiClient(
    private val webClient: WebClient,
    private val currencyApiProperties: CurrencyApiProperties
) {

    @Cacheable("currencyExchangeRates")
    fun currencyExchangeRates(): Mono<CurrencyExchangeRatesResponse> {
        logger.debug { "Retrieving currency exchange rates" }
        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder.path(currencyApiProperties.endpoints.exchangeRates)
                    .queryParam("app_id", currencyApiProperties.apiKey)
                    .build()
            }
            .retrieve()
            .bodyToMono(CurrencyExchangeRatesResponse::class.java)
            .doOnNext {
                logger.debug { "CurrencyConversionResponse: $it" }
            }
    }

    @Cacheable("availableCurrencies")
    fun availableCurrencies(): Mono<Map<String, String>> {
        logger.debug { "Retrieving available currencies" }
        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder.path(currencyApiProperties.endpoints.currencies)
                    .queryParam("app_id", currencyApiProperties.apiKey)
                    .build()
            }
            .retrieve()
            .bodyToMono<Map<String, String>>()
            .doOnNext {
                logger.debug { "Currencies: $it" }
            }
    }

    data class CurrencyExchangeRatesResponse(
        val timestamp: Int,
        val base: String,
        val rates: Map<String, BigDecimal>
    )
}