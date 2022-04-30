package com.traanite.reline.gasprices

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.util.MimeTypeUtils.TEXT_HTML
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

class Scraper {

    fun run() {
        val webClient = WebClient.builder()
            .defaultCookie("my_session_id", "155a9d6d5b07f9b7c45114a0d4192121233")
            .baseUrl("https://www.globalpetrolprices.com/")
            .build()

        val setValueRequest = webClient.post()
            .uri("/gasoline_prices/")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .body(Mono.just("literGalon=1&currency=EUR"), String::class.java)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        val getValueRequest = webClient.post()
            .uri("/gasoline_prices/")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
//        println(getValueRequest)

    }

}