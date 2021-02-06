package net.ambulando.watcher.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.ambulando.watcher.model.Trade
import net.ambulando.watcher.repositories.mongo.OHLCsRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.io.File
import java.nio.charset.Charset

@ExtendWith(MockitoExtension::class)
internal class OHLCServiceTest {

    @Mock
    private lateinit var repository: OHLCsRepository 
            
    @Test
    fun calculateOHLCs() {
        val trades: List<Trade> = jacksonObjectMapper().readValue(File("src/test/resources/kraken/trades_XBTEUR.json").readText(Charset.defaultCharset()))
        val candles = OHLCService(repository).calculateOHLCs(trades.slice(0..16), 1)
        val expected = OHLCService(repository).loadOHLC(1, "src/test/resources/kraken/XBTEUR_1.csv")
        assertThat(candles).isNotEmpty
        assertThat(candles.size).isEqualTo(expected.size)
        assertThat(candles).isEqualTo(expected)
                    
    }

    @Test
    fun loadOHLC() {
        val candles = OHLCService(repository).loadOHLC(1, "src/test/resources/kraken/XBTEUR_1.csv")
        assertThat(candles).isNotEmpty
        assertThat(candles.size) .isEqualTo(15)
    }
}
