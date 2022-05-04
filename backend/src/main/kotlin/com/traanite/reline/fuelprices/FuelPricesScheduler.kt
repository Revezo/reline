package com.traanite.reline.fuelprices

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class FuelPricesScheduler(val fuelPricesUpdater: FuelPricesUpdater) {

    @Scheduled(fixedRate = 600000)
    fun updateDieselPrices() {
        fuelPricesUpdater.updateGasolinePrices()
    }
//
//    @Scheduled(fixedRate = 600000)
//    fun updateGasolinePrices() {
//        scraper.scrape(FuelType.Gasoline)
//    }
}