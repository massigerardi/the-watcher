package net.ambulando.watcher.services.knowm.kraken

import org.knowm.xchange.Exchange
import org.knowm.xchange.ExchangeFactory
import org.knowm.xchange.kraken.KrakenExchange
import org.knowm.xchange.service.marketdata.MarketDataService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KrakenConfig {

    @Bean
    fun krakenMarketDataService(exchange: Exchange): MarketDataService =
        exchange.marketDataService

    @Bean
    fun krakenExchange(): Exchange =
        ExchangeFactory.INSTANCE.createExchange(KrakenExchange::class.java)
}
