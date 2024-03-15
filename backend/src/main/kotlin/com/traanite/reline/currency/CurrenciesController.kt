package com.traanite.reline.currency

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*

@RestController
class CurrenciesController {

    @GetMapping("/currencies")
    fun getCurrencies(): Mono<AvailableCurrenciesResponse> {
        return Mono.just(AvailableCurrenciesResponse(
            Currency.getAvailableCurrencies().map { CurrencyResponse(it.displayName, it.currencyCode)}
        ))
    }

    data class AvailableCurrenciesResponse(val values: List<CurrencyResponse>)
    data class CurrencyResponse(
        val name: String,
        val code: String
    )
}