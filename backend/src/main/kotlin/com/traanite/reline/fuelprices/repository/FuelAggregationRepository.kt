package com.traanite.reline.fuelprices.repository

import com.traanite.reline.fuelprices.model.CountryFuelPriceData
import com.traanite.reline.fuelprices.model.LatestCountryFuelPriceData
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.GroupOperation
import org.springframework.data.mongodb.core.aggregation.MatchOperation
import org.springframework.data.mongodb.core.aggregation.SortOperation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.time.ZonedDateTime

@Repository
class FuelAggregationRepository(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    // todo tests
    fun findLatestFuelPriceData(): Flux<LatestCountryFuelPriceData> {
        val matchOperation: MatchOperation = Aggregation.match(Criteria("createdAt").gt(ZonedDateTime.now().minusMonths(1)))
        val sortOperation: SortOperation = Aggregation.sort(Sort.Direction.ASC, "createdAt")
        val groupOperation: GroupOperation = Aggregation.group("country")
            .last("_id").`as`("id")
            .last("createdAt").`as`("createdAt")
            .last("gasolineData").`as`("gasolineData")
            .last("dieselData").`as`("dieselData")

        val aggregation: Aggregation = Aggregation.newAggregation(matchOperation, sortOperation, groupOperation)
        return reactiveMongoTemplate.aggregate(aggregation, CountryFuelPriceData::class.java, LatestCountryFuelPriceData::class.java)
    }
}