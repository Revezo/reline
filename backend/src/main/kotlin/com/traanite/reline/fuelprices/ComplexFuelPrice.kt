package com.traanite.reline.fuelprices

import java.math.BigDecimal

data class ComplexFuelPrice(val fuelType: FuelType, val averagePrice: BigDecimal, val minimalPrice: BigDecimal, val maximalPrice: BigDecimal) : FuelPrice() {
}