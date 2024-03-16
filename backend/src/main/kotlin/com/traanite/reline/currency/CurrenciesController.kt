package com.traanite.reline.currency

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.util.*

@RestController
class CurrenciesController(private val currencyConverter: CurrencyConverter) {

    @GetMapping("/currencies")
    fun getCurrencies(): Mono<AvailableCurrenciesResponse> {
        return currencyConverter.availableCurrencies()
            .collectList()
            .map { AvailableCurrenciesResponse(it) }
    }

    @GetMapping("/currencies/convert/{amount}/from/{fromCurrency}/to/{toCurrency}")
    fun convertCurrency(
        @PathVariable amount: BigDecimal,
        @PathVariable fromCurrency: Currency,
        @PathVariable toCurrency: Currency
    ): Mono<BigDecimal> {
        return currencyConverter.convertToCurrency(amount, fromCurrency, toCurrency)
    }

    data class AvailableCurrenciesResponse(val values: List<CurrencyData>)
}