package com.traanite.reline.currency

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class CurrencyConverter(private val currencyConversionRateRetriever: CurrencyConversionRateRetriever) {

    fun convertToCurrency(amount: BigDecimal, fromCurrency: Currency, toCurrency: Currency): Mono<BigDecimal> {
        if (fromCurrency == toCurrency) {
            return Mono.just(amount)
        }
        return currencyConversionRateRetriever.currencyConversionRate(fromCurrency, toCurrency)
            .map { it * amount }
            .doOnError {
                logger.error { "Error converting currency. amount=${amount}, " +
                        "fromCurrency=${fromCurrency}, toCurrency=${toCurrency}" }
            }
    }

}