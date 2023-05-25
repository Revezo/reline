package com.traanite.reline.fuelprices.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled

class FuelPricesScheduler(private val fuelPricesUpdater: FuelPricesUpdater) {
    companion object {
        val log: Logger = LoggerFactory.getLogger(FuelPricesScheduler::class.java)
    }

    @Scheduled(fixedRate = 600000)
    fun updateFuelPrices() {
        // todo some operation lock?
        log.debug("Schedule update")
        fuelPricesUpdater.updateGasolinePrices().subscribe()
    }
}