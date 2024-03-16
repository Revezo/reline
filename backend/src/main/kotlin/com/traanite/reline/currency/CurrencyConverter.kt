package com.traanite.reline.currency

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.math.MathContext
import java.util.*
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

@Service
class CurrencyConverter(private val currencyExchangeApiClient: CurrencyExchangeApiClient) {

    // todo save rates in the database, update them every x hours, @Cacheable calls from the database
    @Scheduled(timeUnit = TimeUnit.HOURS, fixedRate = 6)
    private fun initializeCaches() {
        logger.info { "CurrencyConverter initialized" }
        currencyExchangeApiClient.currencyExchangeRates().subscribe()
        currencyExchangeApiClient.availableCurrencies().subscribe()
    }

    fun convertToCurrency(amount: BigDecimal, fromCurrency: Currency, toCurrency: Currency): Mono<BigDecimal> {
        if (fromCurrency == toCurrency) {
            return Mono.just(amount)
        }
        return currencyExchangeApiClient.currencyExchangeRates()
            .map {
                if (it.base == fromCurrency.currencyCode) {
                    it.rates.getOrDefault(toCurrency.currencyCode, BigDecimal.ZERO)
                        .multiply(amount)
                } else {
                    val amountInBaseCurrency = convertToBaseCurrency(it, fromCurrency, amount)
                    it.rates.getOrDefault(toCurrency.currencyCode, BigDecimal.ZERO)
                        .multiply(amountInBaseCurrency)
                }
            }
            .doOnError {
                logger.error {
                    "Error converting currency. amount=${amount}, " +
                            "fromCurrency=${fromCurrency}, toCurrency=${toCurrency}"
                }
            }
    }

    fun availableCurrencies(): Flux<CurrencyData> {
        return currencyExchangeApiClient.availableCurrencies()
            .flatMapIterable { currenciesResponse ->
                currenciesResponse.entries.map { mapEntry ->
                    CurrencyData(mapEntry.key, mapEntry.value)
                }
            }
    }

    private fun convertToBaseCurrency(
        currencyExchangeRates: CurrencyExchangeApiClient.CurrencyExchangeRatesResponse,
        fromCurrency: Currency,
        amount: BigDecimal
    ): BigDecimal {
        return currencyExchangeRates.rates[fromCurrency.currencyCode]
            .let { rate ->
                if (rate == null) {
                    logger.error { "No rate found for currency: ${fromCurrency.currencyCode}" }
                    BigDecimal.ZERO
                } else {
                    amount.divide(rate, MathContext.DECIMAL32)
                }
            }
    }

}

data class CurrencyData(
    val code: String,
    val name: String
)