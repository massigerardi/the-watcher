package net.ambulando.watcher.model

import java.math.BigDecimal
import java.util.*

data class Price(
    val amount: BigDecimal,
    val currency: Currency
)
