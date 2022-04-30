package com.traanite.reline

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("test")
class TestController {

    @GetMapping
    fun testFun(): Mono<String> {
        return Mono.just("a")
    }

}