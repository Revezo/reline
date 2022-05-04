package com.traanite.reline.fuelprices

class FuelPricesStore {

    val gasolinePrices: MutableMap<Country, CountryFuelPriceData> = mutableMapOf()
    val dieselPrices: MutableMap<Country, CountryFuelPriceData> = mutableMapOf()

}