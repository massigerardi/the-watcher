package net.ambulando.watcher.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.ambulando.watcher.model.Trade
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.charset.Charset


internal class OHLCCalculatorServiceTest {

    @Test
    fun calculateOHLCs() {
        val trades: List<Trade> = jacksonObjectMapper().readValue(File("src/test/resources/kraken/trades_XBTEUR.json").readText(Charset.defaultCharset()))
        val candles = OHLCCalculatorService().calculateOHLCs(trades.slice(0..10), 1)
        assertThat(candles).isNotEmpty
                    
    }
}
