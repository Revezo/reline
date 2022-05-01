package com.traanite.reline.gasprices

import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterConfig
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator
import org.jsoup.Jsoup
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.*
import java.util.regex.Pattern

class Scraper {
    private val webClient = WebClient.builder()
        .defaultCookie("my_session_id", UUID.randomUUID().toString())
        .baseUrl("https://www.globalpetrolprices.com/")
        .build()

    private val rateLimiter = RateLimiter.of(
        "my-rate-limiter",
        RateLimiterConfig.custom()
            .limitRefreshPeriod(Duration.ofSeconds(1))
            .limitForPeriod(5)
            .timeoutDuration(Duration.ofMinutes(60))
            .build()
    )

    fun run() {
        webClient.post()
            .uri("/gasoline_prices/")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .body(Mono.just("literGalon=1&currency=EUR"), String::class.java)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        val getValueRequest: String? = webClient.get()
            .uri("/gasoline_prices/")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        val doc = Jsoup.parseBodyFragment(getValueRequest!!)
        val newsHeadlines = doc.select(".graph_outside_link")
        for (headline in newsHeadlines) {
            val uri = headline.attr("href")
//            if (uri.contains("Poland")) {
            webClient.post()
                .uri(uri)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(Mono.just("literGalon=1&currency=EUR"), String::class.java)
                .retrieve()
                .bodyToMono(String::class.java)
                .doOnEach { aaa(uri) }
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .subscribe()
//                }
        }
    }

    private fun aaa(uri: String) {
        webClient.get()
            .uri(uri)
            .retrieve()
            .bodyToMono(String::class.java)
            .subscribe {
                val doc = Jsoup.parseBodyFragment(it!!)
                val newsHeadlines = doc.select("div.tipInfo > div:nth-child(1)")
                val text = newsHeadlines.text()
                println(text)


                val p =
                    Pattern.compile("(.*)The average value for (.*) during that period was (.*) Euro with a minimum of (.*) Euro on (.*) and a maximum of (.*) Euro on (.*).")
                val m = p.matcher(text)
                if (m.find()) {
//                        println("Group 0: ${m.group(0)}")
//                        println("Group 1: ${m.group(1)}")
                    println("Group 2: ${m.group(2)}")
                    println("Group 3: ${m.group(3)}")
                    println("Group 4: ${m.group(4)}")
//                        println("Group 5: ${m.group(5)}")
                    println("Group 6: ${m.group(6)}")
                } else {
                    val p1 = Pattern.compile("(.*): The price of octane-95 gasoline is (.*) Euro per liter.")
                    val m1 = p1.matcher(text)
                    if (m1.find()) {
//                        println("Group 0: ${m.group(0)}")
                        println("Group 1: ${m.group(1)}")
                        println("Group 2: ${m.group(2)}")
                    }
                }
                println()
            }
//        println("aaa: $uri")
    }

}