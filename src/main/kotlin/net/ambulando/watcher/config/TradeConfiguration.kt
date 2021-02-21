package net.ambulando.watcher.config

import org.knowm.xchange.currency.CurrencyPair
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "trades")
class TradeConfiguration {
    lateinit var symbols: List<CurrencyPair>
    lateinit var frequency: String
    lateinit var intervals: List<Int>
}
