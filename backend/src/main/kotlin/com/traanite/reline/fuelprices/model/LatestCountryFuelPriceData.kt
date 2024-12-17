package com.traanite.reline.fuelprices.model

import java.time.ZonedDateTime

data class LatestCountryFuelPriceData(
    val id: Country,
    val createdAt: ZonedDateTime,
    val gasolineData: FuelPrice,
    val dieselData: FuelPrice
)
