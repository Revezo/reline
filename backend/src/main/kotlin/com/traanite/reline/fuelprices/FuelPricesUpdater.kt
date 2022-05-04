package com.traanite.reline.fuelprices

import org.springframework.stereotype.Component

@Component
class FuelPricesUpdater(private val fuelPricesStore: FuelPricesStore) {

    private val dieselUri = "/diesel_prices/"
    private val gasolineUri = "/gasoline_prices/"

    fun updateGasolinePrices() {
//        val scraper = Scraper(fuelPricesStore)
//        scraper.scrape(FuelType.Gasoline, gasolineUri)
    }
}