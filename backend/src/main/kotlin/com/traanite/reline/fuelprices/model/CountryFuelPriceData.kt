package com.traanite.reline.fuelprices.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "FuelPrices")
data class CountryFuelPriceData(
    @Id val country: Country,
    val gasolineData: FuelPrice,
    val dieselData: FuelPrice
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CountryFuelPriceData

        if (country != other.country) return false

        return true
    }

    override fun hashCode(): Int {
        return country.hashCode()
    }
}