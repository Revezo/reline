package com.traanite.reline.fuelprices

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/prices")
class FuelPricesController(val fuelPricesStore: FuelPricesStore) {
    private val gasolineUri = "/gasoline_prices/"
    private val dieselUri = "/diesel_prices/"

    @GetMapping("gas")
    fun getGasolinePrices(): Mono<Map<Country, CountryFuelPriceData>> {
        val scraper = Scraper()
        return scraper.scrape(FuelType.Gasoline, gasolineUri).collectMap { entry -> entry.country}
    }

    @GetMapping("diesel")
    fun getDieselPrices(): Mono<Map<Country, CountryFuelPriceData>> {
        val scraper = Scraper()
        return scraper.scrape(FuelType.Diesel, dieselUri).collectMap { entry -> entry.country}
    }

}