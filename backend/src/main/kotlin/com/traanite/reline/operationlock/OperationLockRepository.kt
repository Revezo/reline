package com.traanite.reline.operationlock

import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class OperationLockRepository(val reactiveMongoTemplate: ReactiveMongoTemplate) {

    fun lockOperation(operationName: String): Mono<OperationLock> {
        return reactiveMongoTemplate.findAndModify(
            Query.query(
                Criteria.where("operationName").`is`(operationName)
                    .and("operationStatus").`is`(OperationStatus.Success)
            ),
            Update().set("operationStatus", OperationStatus.InProgress),
            OperationLock::class.java
        ).switchIfEmpty(Mono.defer {
            reactiveMongoTemplate.insert(
                OperationLock(
                    operationName = operationName,
                    operationStatus = OperationStatus.InProgress
                )
            )}
        )
    }
}