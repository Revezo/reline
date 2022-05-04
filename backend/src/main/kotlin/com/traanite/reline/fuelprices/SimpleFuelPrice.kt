package com.traanite.reline.fuelprices

import java.math.BigDecimal

data class SimpleFuelPrice(val fuelType: FuelType, val price: BigDecimal) : FuelPrice() {

}