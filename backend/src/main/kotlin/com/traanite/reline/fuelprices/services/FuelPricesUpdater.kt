package com.traanite.reline.fuelprices.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class FuelPricesUpdater(
    private val fuelPricesService: FuelPricesService,
    private val pricesScraper: GlobalPetrolPricesScraper
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    fun updateGasolinePrices() {
        log.debug("Start updating procedure")
        val fuelPriceData = pricesScraper.getPrices()
        log.debug("Retrieved prices data")
        fuelPricesService.saveAll(fuelPriceData).subscribe()
        log.debug("Finished update procedure")
    }

}