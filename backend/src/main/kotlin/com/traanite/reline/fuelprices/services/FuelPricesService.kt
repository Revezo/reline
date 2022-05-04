package com.traanite.reline.fuelprices.services

import com.traanite.reline.fuelprices.model.CountryFuelPriceData
import com.traanite.reline.fuelprices.repository.FuelPricesRepository
import reactor.core.publisher.Flux

class FuelPricesService(private val pricesRepository: FuelPricesRepository) {

    fun saveAll(fuelPriceData: Flux<CountryFuelPriceData>): Flux<CountryFuelPriceData> {
        return pricesRepository.saveAll(fuelPriceData)
    }

    fun findAll(): Flux<CountryFuelPriceData> {
        return pricesRepository.findAll()
    }
}