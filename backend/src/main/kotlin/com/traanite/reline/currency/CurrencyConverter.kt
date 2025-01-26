package com.traanite.reline.currency

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.bson.types.ObjectId
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.math.MathContext
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class CurrencyConverter(
    private val currencyExchangeApiClient: CurrencyExchangeApiClient,
    private val currencyExchangeRatesRepository: CurrencyExchangeRatesRepository) {

    @PostConstruct
    private fun init() {
        updateCurrencyPrices()
    }

    @Scheduled(cron = "0 0 4 * * *")
    private fun updateCurrencyPrices() {
        logger.info { "CurrencyConverter initialized" }
        currencyExchangeApiClient.currencyExchangeRates()
            .flatMap {
                val currencyExchangeRates =
                    CurrencyExchangeRates(ObjectId(), it.timestamp, Currency.getInstance(it.base), it.rates)
                currencyExchangeRatesRepository.save(currencyExchangeRates)
            }
            .subscribe()
    }

    fun convertToCurrency(amount: BigDecimal, fromCurrency: Currency, toCurrency: Currency): Mono<BigDecimal> {
        if (fromCurrency == toCurrency) {
            return Mono.just(amount)
        }
        return Mono.defer {
            currencyExchangeRatesRepository.findFirstByOrderByIdDesc()
                .map {
                    if (it.baseCurrency == fromCurrency) {
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
        currencyExchangeRates: CurrencyExchangeRates,
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