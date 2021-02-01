package net.ambulando.watcher.config

import org.knowm.xchange.ExchangeFactory
import org.knowm.xchange.kraken.KrakenExchange
import org.knowm.xchange.service.marketdata.MarketDataService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KrakenConfig {
    
    @Bean
    fun krakenMarketDataService(): MarketDataService =
        ExchangeFactory.INSTANCE.createExchange(KrakenExchange::class.java).marketDataService
}
