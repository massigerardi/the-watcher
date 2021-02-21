package net.ambulando.watcher.services

import net.ambulando.watcher.model.Price
import net.ambulando.watcher.model.Trade
import org.knowm.xchange.currency.CurrencyPair

interface IMarketDataService {

  fun getTrades(pair: CurrencyPair, since: Long): List<Trade>

  fun getPrice(pair: CurrencyPair): Price

}
