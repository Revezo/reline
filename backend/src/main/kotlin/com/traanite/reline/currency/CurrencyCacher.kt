package com.traanite.reline.currency

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.*

@Service
class CurrencyCacher(private val currencyConversionRateRetriever: CurrencyConversionRateRetriever) {

    // todo

    @Scheduled(fixedRate = 3600000)
    fun cacheCurrencyConversionRates() {
        Currency.getAvailableCurrencies()
    }
}