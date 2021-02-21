package net.ambulando.watcher.services

import net.ambulando.watcher.model.Constants.SCALE
import net.ambulando.watcher.model.OHLC
import net.ambulando.watcher.model.Trade
import java.math.BigDecimal
import java.math.RoundingMode

object EntityMapper {


  @JvmName("toOHLC1")
  fun toOHLC(values: List<String>): OHLC = OHLC(
    time = values[0].toLong() * 1000,
    open = BigDecimal.valueOf(values[1].toDouble()).setScale(SCALE, RoundingMode.HALF_EVEN),
    high = BigDecimal.valueOf(values[2].toDouble()).setScale(SCALE, RoundingMode.HALF_EVEN),
    low = BigDecimal.valueOf(values[3].toDouble()).setScale(SCALE, RoundingMode.HALF_EVEN),
    close = BigDecimal.valueOf(values[4].toDouble()).setScale(SCALE, RoundingMode.HALF_EVEN),
    volume = BigDecimal.valueOf(values[5].toDouble()).setScale(SCALE, RoundingMode.HALF_EVEN),
    count = values[6].toInt()
  )

  fun toOHLC(trades: List<Trade>): OHLC =
    OHLC(
      time = trades.last().time,
      open = trades.first().price.setScale(SCALE, RoundingMode.HALF_EVEN),
      close = trades.last().price.setScale(SCALE, RoundingMode.HALF_EVEN),
      high = trades.maxOf { it.price }.setScale(SCALE, RoundingMode.HALF_EVEN),
      low = trades.minOf { it.price }.setScale(SCALE, RoundingMode.HALF_EVEN),
      volume = trades.fold(BigDecimal.ZERO) { acc, trade -> acc + trade.volume }.setScale(SCALE, RoundingMode.HALF_EVEN),
      count = trades.size
    )
}
