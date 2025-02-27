package com.traanite.reline.fuelprices

import com.traanite.reline.fuelprices.services.CountryFuelPriceDataDto
import com.traanite.reline.fuelprices.services.FuelPricesService
import com.traanite.reline.fuelprices.services.FuelPricesUpdater
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/fuelprices")
class FuelPricesController(
    private val fuelPricesService: FuelPricesService,
    private val fuelPricesUpdater: FuelPricesUpdater
) {

    @GetMapping
    fun getStoredGasolinePrices(
        @RequestParam(name = "currencyCode", required = true, defaultValue = "EUR")
        currencyCode: String
    ): Mono<FuelPricesResponse> {
        return fuelPricesService.findAllInWithCurrencyConversion(Currency.getInstance(currencyCode.uppercase()))
            .sort(compareBy { it.country })
            .collectList()
            .map {
                val response = FuelPricesResponse(it)
                log.debug { "Returning fuel prices: $response" }
                response
            }
    }

    @PostMapping("/update")
    fun updateFuelPrices(): Mono<Void> {
        return fuelPricesUpdater.updateFuelPrices().then()
    }

    data class FuelPricesResponse(val values: List<CountryFuelPriceDataDto>)
}
