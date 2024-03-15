package com.traanite.reline.fuelprices.services

import com.traanite.reline.currency.CurrencyConversionRateRetriever
import com.traanite.reline.fuelprices.model.CountryFuelPriceData
import com.traanite.reline.fuelprices.repository.FuelPricesRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import io.vavr.Tuple
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.util.*

private val log = KotlinLogging.logger {}

@Service
class FuelPricesService(
    private val pricesRepository: FuelPricesRepository,
    private val currencyConversionRateRetriever: CurrencyConversionRateRetriever
) {

    fun saveAll(fuelPriceData: Flux<CountryFuelPriceData>): Flux<CountryFuelPriceData> {
        return pricesRepository.saveAll(fuelPriceData)
    }

    fun findAllInWithCurrencyConversion(currency: Currency): Flux<CountryFuelPriceDataDto> {
        log.info { "Finding all fuel prices with currency conversion to $currency" }
        return pricesRepository.findAll()
            .flatMap { Flux.just(it.gasolineData.currency, it.dieselData.currency) }
            .distinct()
            .filter { it != currency }
            .flatMap {
                currencyConversionRateRetriever.currencyConversionRate(it, currency)
                    .flatMap { conversionRate ->
                        log.info { "Conversion rate from $it to $currency: $conversionRate" }
                        Mono.just(
                            Tuple.of(it, conversionRate)
                        )
                    }
            }
            .collectMap({ it._1 }, { it._2 })
            .flatMapMany { currencyConversions ->
                pricesRepository.findAll()
                    .flatMap { toCurrencyConvertedDto(it, currency, currencyConversions) }
            }
    }

    private fun toCurrencyConvertedDto(
        fuelPriceData: CountryFuelPriceData,
        currency: Currency,
        currencyConversions: Map<Currency, BigDecimal>
    ): Mono<CountryFuelPriceDataDto> {
        log.debug { "Mapping: $fuelPriceData to dto with currency: $currency" }
        val convertedGasolinePrice = fuelPriceData.gasolineData.price * currencyConversions.getOrDefault(
            fuelPriceData.gasolineData.currency, BigDecimal.ZERO
        )
        val convertedDieselPrice = fuelPriceData.dieselData.price * currencyConversions.getOrDefault(
            fuelPriceData.dieselData.currency, BigDecimal.ZERO
        )
        return Mono.just(
            CountryFuelPriceDataDto(
                fuelPriceData.country.value,
                currency.currencyCode,
                convertedGasolinePrice,
                convertedDieselPrice
            )
        )
    }
}

data class CountryFuelPriceDataDto(
    val country: String,
    val currencyCode: String,
    val gasolinePrice: BigDecimal,
    val dieselPrice: BigDecimal
)