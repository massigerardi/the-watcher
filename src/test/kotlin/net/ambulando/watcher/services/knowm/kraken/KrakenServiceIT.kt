package net.ambulando.watcher.services.knowm.kraken

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import net.ambulando.watcher.repositories.mongo.OHLCsRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertTimeout
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.service.marketdata.MarketDataService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.io.File
import java.time.Duration

@SpringBootTest
@AutoConfigureDataMongo
internal class KrakenServiceIT {

    companion object {
        val logger = KotlinLogging.logger {  }
    }
    
    @Autowired
    private lateinit var krakenService: KrakenService
    
    @Autowired
    private lateinit var ohlCsRepository: OHLCsRepository
    
    @BeforeEach
    @AfterEach
    fun before() {
        ohlCsRepository.deleteAll()
    }
    
    @Test
    fun getTrade() {
        val  trades = krakenService.getTrades(CurrencyPair.BTC_EUR, 1104541200)
        assertThat(trades).isNotEmpty
        ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(File("src/test/resources/kraken/trades_XBTEUR.json"), trades)
    }

    @Test
    fun getPrice() {
        val price = krakenService.getPrice(CurrencyPair.BTC_EUR)
        assertThat(price).isNotNull
    }
    
    @Test
    fun retrieveTrades() {
        krakenService.retrieveTrades()
        logger.info { "\n\nstart checks\n\n" }
        assertThat(ohlCsRepository.findAll()).isNotEmpty
        assertThat(assertTimeout(Duration.ofSeconds(5)){ ohlCsRepository.findByPairAndInterval(CurrencyPair.BTC_EUR.toString(), 1) }).isNotEmpty
        assertThat(assertTimeout(Duration.ofSeconds(5)){ ohlCsRepository.findByPairAndInterval(CurrencyPair.BTC_EUR.toString(), 5) }).isNotEmpty
        assertThat(assertTimeout(Duration.ofSeconds(5)){ ohlCsRepository.findByPairAndInterval(CurrencyPair.BTC_EUR.toString(), 15) }).isNotEmpty
        assertThat(assertTimeout(Duration.ofSeconds(5)){ ohlCsRepository.findByPairAndInterval(CurrencyPair.BTC_EUR.toString(), 30) }).isNotEmpty
        assertThat(assertTimeout(Duration.ofSeconds(5)){ ohlCsRepository.findByPairAndInterval(CurrencyPair.ETH_EUR.toString(), 1) }).isNotEmpty
        assertThat(assertTimeout(Duration.ofSeconds(5)){ ohlCsRepository.findByPairAndInterval(CurrencyPair.ETH_EUR.toString(), 5) }).isNotEmpty
        assertThat(assertTimeout(Duration.ofSeconds(5)){ ohlCsRepository.findByPairAndInterval(CurrencyPair.ETH_EUR.toString(), 15) }).isNotEmpty
        assertThat(assertTimeout(Duration.ofSeconds(5)){ ohlCsRepository.findByPairAndInterval(CurrencyPair.ETH_EUR.toString(), 30) }).isNotEmpty
    }

}
