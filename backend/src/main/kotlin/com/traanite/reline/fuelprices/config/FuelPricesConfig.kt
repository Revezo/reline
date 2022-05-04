package com.traanite.reline.fuelprices.config

import com.traanite.reline.fuelprices.services.GlobalPetrolPricesScraper
import com.traanite.reline.fuelprices.repository.FuelPricesRepository
import com.traanite.reline.fuelprices.services.FuelPricesScheduler
import com.traanite.reline.fuelprices.services.FuelPricesUpdater
import com.traanite.reline.fuelprices.services.FuelPricesService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FuelPricesConfig {

    @Bean
    fun fuelPricesScheduler(itemRepository: FuelPricesRepository): FuelPricesScheduler {
        return FuelPricesScheduler(fuelPricesUpdater(itemRepository))
    }

    @Bean
    fun fuelPricesUpdater(itemRepository: FuelPricesRepository): FuelPricesUpdater {
        return FuelPricesUpdater(fuelPricesService(itemRepository), GlobalPetrolPricesScraper())
    }

    @Bean
    fun fuelPricesService(itemRepository: FuelPricesRepository): FuelPricesService {
        return FuelPricesService(itemRepository)
    }

}