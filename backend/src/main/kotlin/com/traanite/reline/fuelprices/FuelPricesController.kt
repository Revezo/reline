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
        return fuelPricesService.findAll().collectList().map { toFuelPricesResponse(it) }
    }

    private fun toFuelPricesResponse(fuelPricesData: List<CountryFuelPriceData>): FuelPricesResponse {
        val pricesData = fuelPricesData.map {
            CountryFuelPriceDataResponse(
                it.country.value,
                it.gasolineData.price,
                it.dieselData.price)
        }
        return FuelPricesResponse(pricesData)
    }

}

data class FuelPricesResponse(val values: List<CountryFuelPriceDataResponse>) {
}

data class CountryFuelPriceDataResponse(
    val country: String,
    val gasolinePrice: BigDecimal,
    val dieselPrice: BigDecimal
) {
}