package com.traanite.reline.fuelprices.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled

class FuelPricesScheduler(private val fuelPricesUpdater: FuelPricesUpdater) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)
    @Scheduled(fixedRate = 600000)
    fun updateFuelPrices() {
        log.debug("Schedule update")
        fuelPricesUpdater.updateGasolinePrices()
    }

}