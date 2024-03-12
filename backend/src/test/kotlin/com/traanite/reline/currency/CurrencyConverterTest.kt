package com.traanite.reline.currency

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier
import java.math.BigDecimal
import java.util.*

@SpringBootTest
class CurrencyConverterTest {

    @Autowired
    lateinit var currencyConverter: CurrencyConverter

    @Test
    fun convertToCurrency() {
        val requestAmount = BigDecimal(100.5)
        val result = currencyConverter.convertToCurrency(
            requestAmount,
            Currency.getInstance("USD"),
            Currency.getInstance("PLN")
        )

        StepVerifier.create(result)
            .assertNext {
                Assertions.assertThat(it).isNotNull
                Assertions.assertThat(it).isGreaterThan(BigDecimal.ZERO)
                Assertions.assertThat(it).isNotEqualTo(requestAmount)
            }
            .verifyComplete()
    }
}