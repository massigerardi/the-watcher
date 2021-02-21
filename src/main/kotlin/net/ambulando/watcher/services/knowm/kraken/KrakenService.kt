package net.ambulando.watcher.services.knowm.kraken

import mu.KotlinLogging
import net.ambulando.watcher.TimeUtils.normalizeTime
import net.ambulando.watcher.config.Constants.H24
import net.ambulando.watcher.config.TradeConfiguration
import net.ambulando.watcher.model.Log
import net.ambulando.watcher.model.Price
import net.ambulando.watcher.model.Trade
import net.ambulando.watcher.model.Type
import net.ambulando.watcher.repositories.mongo.LogRepository
import net.ambulando.watcher.services.IMarketDataService
import net.ambulando.watcher.services.OHLCService
import net.ambulando.watcher.services.knowm.KnowmMapper
import org.knowm.xchange.Exchange
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.service.marketdata.MarketDataService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.Date


@Service
class KrakenService(
  @Autowired
  @Qualifier("krakenMarketDataService")
  val dataService: MarketDataService,
  @Autowired
  @Qualifier("krakenExchange")
  val exchange: Exchange,
  val mapper: KnowmMapper,
  val tradeConfiguration: TradeConfiguration,
  val logRepository: LogRepository,
  val ohlcService: OHLCService
) : IMarketDataService {

  companion object {
    val logger = KotlinLogging.logger { KrakenService::class.java.name }
  }

  override fun getTrades(pair: CurrencyPair, since: Long): List<Trade> =
    dataService.getTrades(pair, since).trades.map { mapper.toTrade(it) }.toList()

  override fun getPrice(pair: CurrencyPair): Price =
    dataService.getTicker(pair).let { mapper.toPrice(it) }

  @Scheduled(cron = "\${trades.frequency}")
  @Transactional
  fun retrieveTrades() {
    val exchangeName = exchange.exchangeSpecification.exchangeName
    exchange.exchangeSymbols.intersect(tradeConfiguration.symbols).forEach { pair ->
      try {
        val log = logRepository.findTopByPairAndExchangeAndIsErrorAndTypeOrderByTimestampDesc(pair.toString(), exchange = exchangeName, type = Type.TRADE)
        val since = log?.timestamp ?: Instant.now().toEpochMilli().minus(H24)
        logger.info { "loading trades for $pair since ${Date(since)} " }
        getTrades(pair, since).run {
          logRepository.save(Log(exchange = exchangeName, message = "", timestamp = normalizeTime(Instant.now().toEpochMilli()), pair = pair.toString(), type = Type.TRADE))
          generateOHLC(trades = this, pair = pair.toString(), exchange = exchangeName)
        }

      } catch (e: Exception) {
        logger.error("error ", e)
        logRepository.save(
          Log(
            exchange = exchangeName,
            message = "${e.message}",
            isError = true,
            timestamp = normalizeTime(Instant.now().toEpochMilli()),
            pair = pair.toString(),
            type = Type.TRADE
          )
        )
      }
    }
  }

  private fun generateOHLC(trades: List<Trade>, pair: String, exchange: String) {
    tradeConfiguration.intervals.forEach {
      generateOHLC(trades, it, pair, exchange)
    }
  }

  private fun generateOHLC(trades: List<Trade>, interval: Int, pair: String, exchange: String) {
    ohlcService.loadFromTrades(trades, interval, pair, exchange)
  }

}

