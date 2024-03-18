package com.traanite.reline.config

import com.traanite.reline.common.ZonedDateTimeReadConverter
import com.traanite.reline.common.ZonedDateTimeWriteConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.convert.MongoCustomConversions


@Configuration
class MongoDbConfig {

    @Bean
    fun mongoCustomConversions(): MongoCustomConversions {
        return MongoCustomConversions(
            listOf(
                ZonedDateTimeReadConverter(),
                ZonedDateTimeWriteConverter()
            )
        )
    }
}