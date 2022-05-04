package com.traanite.reline.fuelprices

import com.traanite.reline.fuelprices.model.CountryFuelPriceData
import com.traanite.reline.fuelprices.services.FuelPricesService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.math.BigDecimal

@RestController
@RequestMapping("/fuelprices")
class FuelPricesController(private val fuelPricesService: FuelPricesService) {

    @GetMapping
    fun getStoredGasolinePrices(): Mono<FuelPricesResponse> {
        return fuelPricesService.findAll().collectList().map { FuelPricesResponse(it) }
    }
}

data class FuelPricesResponse(val values: List<CountryFuelPriceData>) {
}

data class CountryFuelPriceDataResponse(
    val country: String,
    val gasolineData: FuelPriceResponse,
    val dieselData: FuelPriceResponse
) {
}

data class ComplexFuelPriceResponse(
    val averagePrice: BigDecimal,
    val minimalPrice: BigDecimal,
    val maximalPrice: BigDecimal
) :
    FuelPriceResponse(averagePrice) {
}

data class SimpleFuelPriceResponse(val averagePrice: BigDecimal) : FuelPriceResponse(averagePrice) {
}

abstract class FuelPriceResponse(averagePrice: BigDecimal)