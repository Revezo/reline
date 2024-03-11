package com.traanite.reline.currency

import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class CurrencyConverter {

    fun convertToCurrency(amount: BigDecimal, fromCurrency: String, toCurrency: String): BigDecimal {
        if (fromCurrency == toCurrency) {
            return amount
        }
        // todo implement currency conversion service
        return amount
    }
}

