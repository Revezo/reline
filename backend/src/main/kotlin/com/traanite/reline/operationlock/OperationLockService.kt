package com.traanite.reline.operationlock

import com.traanite.reline.common.Result
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


private val logger = KotlinLogging.logger {}
@Service
class OperationLockService(val operationLockRepository: OperationLockRepository) {

    fun lockOperation(operationName: String): Mono<Result> {
        return operationLockRepository.lockOperation(operationName)
            .map { Result.Success }
            .onErrorResume {
                logger.debug(it) { "Failed to lock operation $operationName" }
                Mono.just(Result.Failure) }
    }

    fun unlockSuccessfulOperation(operationName: String) {

    }

    fun unlockUnsuccessfulOperation(operationName: String) {

    }
}