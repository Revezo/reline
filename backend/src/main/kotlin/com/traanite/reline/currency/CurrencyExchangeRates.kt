package com.traanite.reline.currency

import org.bson.types.ObjectId
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.util.*

@Document(collection = "CurrencyExchangeRates")
data class CurrencyExchangeRates(
    @Id
    val id: ObjectId,
    val timestamp: Int,
    val baseCurrency: Currency,
    val rates: Map<String, BigDecimal>)


@Repository
interface CurrencyExchangeRatesRepository : ReactiveMongoRepository<CurrencyExchangeRates, ObjectId> {
    @Cacheable("CurrencyExchangeRates")
    fun findFirstByOrderByIdDesc(): Mono<CurrencyExchangeRates>
}
