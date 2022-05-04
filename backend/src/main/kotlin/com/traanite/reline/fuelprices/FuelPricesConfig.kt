package com.traanite.reline.fuelprices

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FuelPricesConfig {

    @Bean
    fun fuelPricesStore(): FuelPricesStore {
        return FuelPricesStore()
    }
}