package com.traanite.reline.fuelprices.services

import com.traanite.reline.fuelprices.model.CountryFuelPriceData
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux

class FuelPricesUpdater(
    private val fuelPricesService: FuelPricesService,
    private val pricesScraper: GlobalPetrolPricesScraper
) {
    companion object {
        val log: Logger = LoggerFactory.getLogger(GlobalPetrolPricesScraper::class.java)
    }

    fun updateGasolinePrices(): Flux<CountryFuelPriceData> {
        log.debug("Start updating procedure")
        val fuelPriceData = pricesScraper.getPrices()
        log.debug("Retrieved prices data")
        return fuelPricesService.saveAll(fuelPriceData)
    }
}