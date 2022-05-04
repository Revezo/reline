package com.traanite.reline.fuelprices.repository

import com.traanite.reline.fuelprices.model.Country
import com.traanite.reline.fuelprices.model.CountryFuelPriceData
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FuelPricesRepository : ReactiveCrudRepository<CountryFuelPriceData, Country>