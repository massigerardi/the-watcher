package net.ambulando.watcher.services

import net.ambulando.watcher.TimeUtils
import net.ambulando.watcher.model.Price
import net.ambulando.watcher.model.Trade
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.service.marketdata.MarketDataService
import org.springframework.stereotype.Service
import java.util.*
import kotlin.streams.toList

@Service
class KrakenService(
    val krakenMarketDataService: MarketDataService
) {
    
    fun getTrade(pair: String, since: Long): List<Trade> =
        krakenMarketDataService.getTrades(CurrencyPair.BTC_EUR, since).trades.stream().map { Trade(time = TimeUtils.normalizeTime(it.timestamp.time), price = it.price, volume = it.originalAmount) }.toList()
    
    fun getPrice(pair: String): Price =
        krakenMarketDataService.getTicker(CurrencyPair.BTC_EUR).let { Price(it.last, Currency.getInstance("EUR")) }
}

