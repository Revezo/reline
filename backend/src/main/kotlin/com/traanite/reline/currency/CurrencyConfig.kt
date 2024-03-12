package com.traanite.reline.currency

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import java.util.*


@Configuration
class CurrencyConfig(private val currencyApiProperties: CurrencyApiProperties) {

    @Bean
    fun currencyApiWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl(currencyApiProperties.baseUrl)
            .defaultHeaders { headers: HttpHeaders ->
                headers.accept = Collections.singletonList(MediaType.APPLICATION_JSON)
                headers["X-Api-Key"] = currencyApiProperties.apiKey
            }
            .build()
    }
}

@ConfigurationProperties(prefix = "currency.api")
data class CurrencyApiProperties(
    val baseUrl: String,
    val apiKey: String,
    val endpoint: String
)