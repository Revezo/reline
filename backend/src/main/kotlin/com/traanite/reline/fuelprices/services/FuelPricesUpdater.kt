package com.traanite.reline.fuelprices.services

import com.traanite.reline.fuelprices.model.CountryFuelPriceData
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class FuelPricesUpdater(
    private val fuelPricesService: FuelPricesService,
    private val pricesScraper: GlobalPetrolPricesScraper
) {
    companion object {
        val log: Logger = LoggerFactory.getLogger(GlobalPetrolPricesScraper::class.java)
    }

    @Scheduled(cron = "\${fuel-prices.scheduled-update.cron}")
    private fun updateFuelPricesScheduledTask() {
        log.debug("Updating fuel prices")
        updateFuelPrices().subscribe()
    }

    fun updateFuelPrices(): Flux<CountryFuelPriceData> {
        log.debug("Start updating procedure")
        val fuelPriceData = pricesScraper.getPrices()
        return fuelPricesService.saveAll(fuelPriceData)
    }
}