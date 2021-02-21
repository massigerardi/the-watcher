package net.ambulando.watcher.services

import mu.KotlinLogging
import net.ambulando.watcher.TimeUtils.normalizeTime
import net.ambulando.watcher.model.*
import net.ambulando.watcher.repositories.mongo.LogRepository
import net.ambulando.watcher.repositories.mongo.OHLCsRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.nio.charset.Charset
import java.time.Duration
import java.util.*
import net.ambulando.watcher.services.EntityMapper as mapper

@Service
class OHLCService(
  val ohlcsRepository: OHLCsRepository,
  val logRepository: LogRepository
) {

  companion object {
    val logger = KotlinLogging.logger { }
  }

  @Transactional
  fun loadFromTrades(trades: List<Trade>, interval: Int, pair: String, exchange: String): OHLCs {
    val candles = mutableListOf<OHLC>()
    var (match, rest) = partition(trades, interval)
    while (match.isNotEmpty()) {
      candles.add(mapper.toOHLC(match))
      val (m, r) = partition(rest, interval)
      match = m
      rest = r
    }
    return candles.let {
      logger.info { "saving ${it.size} for $pair at $interval" }
      return OHLCs(interval = interval, ohlcs = it, id = UUID.randomUUID().toString(), pair = pair, exchange = exchange).apply {
        ohlcsRepository.save(this)
        logRepository.save(
          Log(
            id = UUID.randomUUID().toString(),
            timestamp = normalizeTime(it.sortedBy { it.time }.last().time),
            message = "Loaded $pair for $interval from $exchange",
            pair = pair,
            exchange = exchange,
            type = Type.OHLC
          )
        )
      }
    }
  }

  fun loadFromCSVs(files: List<File>, exchange: String): List<OHLCs> =
    files.map {
      val (pair, interval) = it.toPath().fileName.toString().substringBeforeLast(".").split("_").zipWithNext().first()
      loadFromCSV(interval = interval.toInt(), file = it, pair = pair, exchange = exchange)
    }


  fun loadFromCSV(interval: Int, file: File, pair: String, exchange: String): OHLCs =
    file.bufferedReader(Charset.defaultCharset()).readLines()
      .map { it.split(",") }
      .map { mapper.toOHLC(it) }
      .let {
        return OHLCs(interval = interval, ohlcs = it, id = UUID.randomUUID().toString(), pair = pair, exchange = exchange).apply {
          ohlcsRepository.save(this)
          logRepository.save(
            Log(
              id = UUID.randomUUID().toString(),
              timestamp = it.sortedBy { it.time }.last().time,
              message = "Loaded $pair for $interval from $exchange",
              pair = pair,
              exchange = exchange,
              type = Type.OHLC
            )
          )
        }
      }

  fun load(interval: Int, pair: String, exchange: String, from: Long, to: Long): List<OHLC> = ohlcsRepository.findOHLCBetween(interval, pair, exchange, from, to).ohlcs

  private fun partition(trades: List<Trade>, interval: Int): Pair<List<Trade>, List<Trade>> {
    if (trades.isEmpty()) return Pair(trades, trades)
    val orderedTrades = trades.sortedBy { it.time }
    val start = orderedTrades.first()
    val limit = start.time + Duration.ofMinutes(interval.toLong()).toMillis()
    return orderedTrades.partition { it.time < limit }
  }

}
