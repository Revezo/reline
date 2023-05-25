package com.traanite.reline.fuelprices

import com.traanite.reline.fuelprices.model.*
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
        return fuelPricesService.findAll().collectList().map { toFuelPricesResponse(it) }
    }

    private fun toFuelPricesResponse(fuelPricesData: List<CountryFuelPriceData>): FuelPricesResponse {
        val pricesData = fuelPricesData.map {
            CountryFuelPriceDataResponse(it.country.value,
                toFuelPriceDataResponse(it.gasolineData),
                toFuelPriceDataResponse(it.gasolineData))
        }
        return FuelPricesResponse(pricesData)
    }

    private fun toFuelPriceDataResponse(fuelPrice: FuelPrice) : FuelPriceResponse {
        return when (fuelPrice) {
            is SimpleFuelPrice -> FuelPriceResponse(fuelPrice.price, BigDecimal(-1), BigDecimal(-1))
            is ComplexFuelPrice -> FuelPriceResponse(
                fuelPrice.averagePrice,
                fuelPrice.minimalPrice,
                fuelPrice.maximalPrice
            )
            else -> FuelPriceResponse(BigDecimal(-1), BigDecimal(-1), BigDecimal(-1))
        }
    }

}

data class FuelPricesResponse(val values: List<CountryFuelPriceDataResponse>) {
}

data class CountryFuelPriceDataResponse(
    val country: String,
    val gasolineData: FuelPriceResponse,
    val dieselData: FuelPriceResponse
) {
}

data class FuelPriceResponse(
    val averagePrice: BigDecimal,
    val minimalPrice: BigDecimal,
    val maximalPrice: BigDecimal
) {}