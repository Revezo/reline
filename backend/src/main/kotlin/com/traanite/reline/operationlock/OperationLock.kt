package com.traanite.reline.operationlock

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class OperationLock(
    @Id val id: ObjectId,
    @Indexed(unique = true) val operationName: String,
    val lockDate: LocalDateTime,
    val operationStatus: OperationStatus
) {
    constructor(operationName: String, operationStatus: OperationStatus) : this(
        ObjectId.get(),
        operationName,
        LocalDateTime.now(),
        operationStatus
    )
}

enum class OperationStatus {
    Success, Failed, InProgress
}
