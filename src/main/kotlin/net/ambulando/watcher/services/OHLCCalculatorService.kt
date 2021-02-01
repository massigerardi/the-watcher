package net.ambulando.watcher.services

import net.ambulando.watcher.model.OHLC
import net.ambulando.watcher.model.Trade
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Duration

@Service
class OHLCCalculatorService {

    fun calculateOHLCs(trades: List<Trade>, interval: Long):List<OHLC> {
        val candles = mutableListOf<OHLC>()
        var (match, rest) = partition(trades, interval)
        while (match.isNotEmpty()) {
            candles.add(calculateOHLC(match))
            var (m, r) = partition(rest, interval)
            match = m
            rest = r
        }
        return candles
    }

    private fun partition(trades: List<Trade>, interval: Long): Pair<List<Trade>, List<Trade>> {
        if (trades.isEmpty()) return Pair(trades, trades)
        val orderedTrades = trades.sortedBy { it.time } 
        val start = orderedTrades.first()
        val limit = start.time+Duration.ofMinutes(interval).toMillis()
        return orderedTrades.partition { it.time < limit }
    }

    private fun calculateOHLC(trades: List<Trade>): OHLC {
        return OHLC(
            time = trades.last().time,
            open = trades.first().price,
            close = trades.last().price,
            high = trades.maxOf { it.price },
            low = trades.minOf { it.price },
            volume = trades.fold(BigDecimal.ZERO) { acc, trade -> acc + trade.volume },
            count = trades.size
        )
    }

}
