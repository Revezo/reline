package com.traanite.reline.fuelprices.model

import java.math.BigDecimal
import java.util.Currency

data class FuelPrice(val fuelType: FuelType, val price: BigDecimal, val currency: Currency) {
    constructor(fuelType: FuelType, currency: Currency) : this(fuelType, BigDecimal.ZERO, currency)

}


