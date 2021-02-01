package net.ambulando.watcher.model

import java.math.BigDecimal

data class Trade(
    val price: BigDecimal,
    val volume: BigDecimal,
    val time: Long
)
