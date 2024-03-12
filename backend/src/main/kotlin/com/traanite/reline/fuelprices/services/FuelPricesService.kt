package com.traanite.reline.fuelprices.services

import com.traanite.reline.currency.CurrencyConverter
import com.traanite.reline.fuelprices.model.CountryFuelPriceData
import com.traanite.reline.fuelprices.repository.FuelPricesRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.util.*

@Service
class FuelPricesService(private val pricesRepository: FuelPricesRepository,
                        private val currencyConverter: CurrencyConverter) {

    fun saveAll(fuelPriceData: Flux<CountryFuelPriceData>): Flux<CountryFuelPriceData> {
        return pricesRepository.saveAll(fuelPriceData)
    }

    fun findAllInWithCurrencyConversion(currency: Currency): Flux<CountryFuelPriceDataDto> {
        return pricesRepository.findAll().flatMap { toCurrencyConvertedDto(it, currency) }
    }

    private fun toCurrencyConvertedDto(fuelPriceData: CountryFuelPriceData, currency: Currency): Mono<CountryFuelPriceDataDto> {
        return currencyConverter.convertToCurrency(
            fuelPriceData.gasolineData.price,
            fuelPriceData.gasolineData.currency,
            currency
        ).zipWith(
            currencyConverter.convertToCurrency(
                fuelPriceData.dieselData.price,
                fuelPriceData.dieselData.currency,
                currency
            )
        ).map {
            CountryFuelPriceDataDto(
                fuelPriceData.country.value,
                currency.currencyCode,
                it.t1,
                it.t2
            )
        }
    }
}

data class CountryFuelPriceDataDto(
    val country: String,
    val currencyCode: String,
    val gasolinePrice: BigDecimal,
    val dieselPrice: BigDecimal
)