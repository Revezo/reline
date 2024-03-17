package com.traanite.reline.fuelprices.services

import com.traanite.reline.fuelprices.model.CountryFuelPriceData
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

private val log = KotlinLogging.logger { }

@Service
class FuelPricesUpdater(
    private val fuelPricesService: FuelPricesService,
    private val pricesScraper: GlobalPetrolPricesScraper
) {

    @Scheduled(cron = "\${fuel-prices.scheduled-update.cron}")
    private fun updateFuelPricesScheduledTask() {
        log.debug { "Updating fuel prices" }
        updateFuelPrices().subscribe()
    }

    fun updateFuelPrices(): Flux<CountryFuelPriceData> {
        log.debug { "Start updating procedure" }
        val fuelPriceData = pricesScraper.getPrices()
        return fuelPricesService.saveAll(fuelPriceData)
    }
}