package com.traanite.reline.fuelprices.model

import java.math.BigDecimal
import java.util.Currency

interface FuelPrice {
    fun fuelType(): FuelType
}

data class EmptyFuelPrice(val fuelType: FuelType) : FuelPrice {
    override fun fuelType(): FuelType {
        return fuelType
    }
}

data class SimpleFuelPrice(val fuelType: FuelType, val price: BigDecimal, val currency: Currency) : FuelPrice {
    override fun fuelType(): FuelType {
        return fuelType
    }
}

data class ComplexFuelPrice(
    val fuelType: FuelType,
    val averagePrice: BigDecimal,
    val minimalPrice: BigDecimal,
    val maximalPrice: BigDecimal,
    val currency: Currency
) : FuelPrice {
    override fun fuelType(): FuelType {
        return fuelType
    }
}