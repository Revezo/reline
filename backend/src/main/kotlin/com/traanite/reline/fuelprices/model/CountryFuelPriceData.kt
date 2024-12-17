package com.traanite.reline.fuelprices.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.ZonedDateTime

@Document(collection = "FuelPrices")
data class CountryFuelPriceData(
    @Id
    val id: ObjectId,
    @Indexed(unique = false)
    @CreatedDate
    val createdAt: ZonedDateTime,
    @Indexed(unique = false)
    val country: Country,
    val gasolineData: FuelPrice,
    val dieselData: FuelPrice
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CountryFuelPriceData

        return country == other.country
    }

    override fun hashCode(): Int {
        return country.hashCode()
    }
}