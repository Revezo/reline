package com.traanite.reline.currency

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class CurrencyConversionRateRetriever(private val webClient: WebClient,
                                      private val currencyApiProperties: CurrencyApiProperties) {

    @Cacheable("currencyConversionRate")
    fun currencyConversionRate(fromCurrency: Currency, toCurrency: Currency): Mono<BigDecimal> {
        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder.path(currencyApiProperties.endpoint)
                    .queryParam("want", toCurrency.currencyCode)
                    .queryParam("have", fromCurrency.currencyCode)
                    .queryParam("amount", 1)
                    .build()
            }
            .retrieve()
            .bodyToMono(CurrencyConversionResponse::class.java)
            .map {
                logger.debug { "CurrencyConversionResponse: $it" }
                it.newAmount
            }
    }

    data class CurrencyConversionResponse(
        @JsonProperty("new_amount") val newAmount: BigDecimal,
        @JsonProperty("new_currency") val newCurrency: String,
        @JsonProperty("old_currency") val oldCurrency: String,
        @JsonProperty("old_amount") val oldAmount: BigDecimal
    )
}