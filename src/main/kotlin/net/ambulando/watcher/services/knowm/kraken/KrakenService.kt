package net.ambulando.watcher.services.knowm.kraken

import net.ambulando.watcher.model.Price
import net.ambulando.watcher.model.Trade
import net.ambulando.watcher.services.knowm.KnowmMapper
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.service.marketdata.MarketDataService
import org.springframework.stereotype.Service

@Service
class KrakenService(
    val krakenMarketDataService: MarketDataService,
    val mapper: KnowmMapper
) {
    
    fun getTrade(pair: String, since: Long): List<Trade> =
        krakenMarketDataService.getTrades(CurrencyPair.BTC_EUR, since).trades.map { mapper.toTrade(it) }.toList()
    
    fun getPrice(pair: String): Price =
        krakenMarketDataService.getTicker(CurrencyPair.BTC_EUR).let { mapper.toPrice(it) }
}

