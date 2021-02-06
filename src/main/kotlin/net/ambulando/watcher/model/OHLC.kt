package net.ambulando.watcher.model

import lombok.EqualsAndHashCode
import java.math.BigDecimal

@EqualsAndHashCode
data class OHLC(
    val time: Long,
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: BigDecimal,
    val count: Int) {
}
