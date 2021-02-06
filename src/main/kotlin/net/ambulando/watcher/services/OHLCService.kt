package net.ambulando.watcher.services

import net.ambulando.watcher.model.OHLC
import net.ambulando.watcher.model.OHLCs
import net.ambulando.watcher.model.Trade
import net.ambulando.watcher.repositories.mongo.OHLCsRepository
import org.springframework.stereotype.Service
import java.io.File
import java.nio.charset.Charset
import java.time.Duration
import java.util.*
import net.ambulando.watcher.services.EntityMapper as mapper

@Service
class OHLCService(
    val repository: OHLCsRepository
) {

    fun calculateOHLCs(trades: List<Trade>, interval: Long):List<OHLC> {
        val candles = mutableListOf<OHLC>()
        var (match, rest) = partition(trades, interval)
        while (match.isNotEmpty()) {
            candles.add(mapper.toOHLC(match))
            var (m, r) = partition(rest, interval)
            match = m
            rest = r
        }
        return candles
    }
    
    fun loadOHLC(interval: Int, filePath: String): List<OHLC> = 
        File(filePath).bufferedReader(Charset.defaultCharset()).readLines()
            .map { it.split(",") }
            .map { mapper.toOHLC(it) }
            .let { 
                repository.save(OHLCs(interval = interval, ohlcs = it, id = UUID.randomUUID().toString()))
                it
            }
    
    fun load(from: Long, to:Long) {
        
    }
    
    
    private fun partition(trades: List<Trade>, interval: Long): Pair<List<Trade>, List<Trade>> {
        if (trades.isEmpty()) return Pair(trades, trades)
        val orderedTrades = trades.sortedBy { it.time } 
        val start = orderedTrades.first()
        val limit = start.time+Duration.ofMinutes(interval).toMillis()
        return orderedTrades.partition { it.time < limit }
    }

}
