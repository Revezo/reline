package com.traanite.reline.fuelprices

import com.traanite.reline.fuelprices.services.CountryFuelPriceDataDto
import com.traanite.reline.fuelprices.services.FuelPricesService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/fuelprices")
class FuelPricesController(private val fuelPricesService: FuelPricesService) {

    @GetMapping
    fun getStoredGasolinePrices(
        @RequestParam(name = "currencyCode", required = false, defaultValue = "EUR")
        currencyCode: String): Mono<FuelPricesResponse> {

        return fuelPricesService.findAllInWithCurrencyConversion(Currency.getInstance(currencyCode))
            .collectList()
            .map { FuelPricesResponse(it) }
    }

    data class FuelPricesResponse(val values: List<CountryFuelPriceDataDto>) {
    }
}
