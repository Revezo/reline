package com.traanite.reline.operationlock

import com.traanite.reline.common.Result
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/operationLocks")
class OperationLockController(val operationLockService: OperationLockService) {

    @PostMapping("/{operationName}")
    fun lockOperation(@PathVariable operationName: String): Mono<Result> {
        return operationLockService.lockOperation(operationName)
    }
}