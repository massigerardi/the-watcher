package net.ambulando.watcher.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.nhaarman.mockitokotlin2.*
import com.sun.jna.platform.win32.Netapi32Util
import net.ambulando.watcher.model.Log
import net.ambulando.watcher.model.OHLC
import net.ambulando.watcher.model.OHLCs
import net.ambulando.watcher.model.Trade
import net.ambulando.watcher.repositories.mongo.LogRepository
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
    private lateinit var ohlcsRepository: OHLCsRepository
    
    @Mock
    private lateinit var logRepository: LogRepository
    
    
    private fun service() = OHLCService(ohlcsRepository, logRepository)
    
    @Test
    fun `should load from trades`() {
        val trades: List<Trade> = jacksonObjectMapper().readValue(File("src/test/resources/kraken/trades_XBTEUR.json").readText(Charset.defaultCharset()))
        val result = service().loadFromTrades(trades.slice(0..16), 1, "XBTEUR", "TEST_EXC")
        val expected = service().loadFromCSV(1, File("src/test/resources/kraken/XBTEUR_1.csv"), "XBTEUR", "TEST_EXC")
        assertThat(result).isNotNull
        assertThat(result.ohlcs.size).isEqualTo(expected.ohlcs.size)
        assertThat(result.ohlcs).isEqualTo(expected.ohlcs)
        verify(ohlcsRepository, atLeastOnce()).save(any())
        argumentCaptor<Log> {
            verify(logRepository, atLeastOnce()).save(capture())
            assertThat(secondValue.timestamp).isEqualTo(1379191920000)
        }
    }

    @Test
    fun `should return list of OHLC between timestamps`() {
        whenever(ohlcsRepository.findOHLCBetween(eq(1440), any(), any(), any(), any())).then { OHLCs(ohlcs = listOf<OHLC>()) }
        assertThat(service().load(1440, "XBTEUR", "TEST_EXC",1383523200000, 1383264000000)).isNotNull
        assertThat(service().load(1440, "XBTEUR", "TEST_EXC", 1383523200000, 1383264000000)).isEmpty()
    }
    
    @Test
    fun `should load from a list of csv files`() {
        val result = service().loadFromCSVs(File("src/test/resources/kraken/XBTEUR").listFiles().toList(), "test")
        assertThat(result).isNotEmpty
        assertThat(result.size).isEqualTo(6)
        verify(ohlcsRepository, times(6)).save(any())
    }
    
    @Test
    fun `should load from csv`() {
        val result = service().loadFromCSV(1, File("src/test/resources/kraken/XBTEUR_1.csv"), "XBTEUR", "TEST_EXC")
        assertThat(result).isNotNull
        assertThat(result.ohlcs.size) .isEqualTo(15)
        verify(ohlcsRepository, atLeastOnce()).save(any())
        argumentCaptor<Log> {
            verify(logRepository, atLeastOnce()).save(capture())
            assertThat(firstValue.timestamp).isEqualTo(1379191920000)
        }
    }
}
