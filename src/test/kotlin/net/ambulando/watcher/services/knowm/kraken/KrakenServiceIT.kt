package net.ambulando.watcher.services.knowm.kraken

import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import mu.KotlinLogging
import net.ambulando.watcher.repositories.mongo.OHLCsRepository
import net.ambulando.watcher.utils.JsonUtils.readFromFileAsMap
import net.ambulando.watcher.utils.TestValues.ticker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.knowm.xchange.Exchange
import org.knowm.xchange.ExchangeSpecification
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.dto.Order
import org.knowm.xchange.dto.marketdata.Trade
import org.knowm.xchange.dto.marketdata.Trades
import org.knowm.xchange.kraken.KrakenExchange
import org.knowm.xchange.service.marketdata.MarketDataService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.annotation.DirtiesContext
import java.math.BigDecimal
import java.util.*

@SpringBootTest
@AutoConfigureDataMongo
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
internal class KrakenServiceIT {

  companion object {
    val logger = KotlinLogging.logger { }
  }

  @Autowired
  private lateinit var krakenService: KrakenService

  @Autowired
  private lateinit var ohlCsRepository: OHLCsRepository

  @MockBean
  private lateinit var krakenMarketDataService: MarketDataService

  @MockBean
  private lateinit var krakenExchange: Exchange

  @BeforeEach
  fun before() {
    whenever(krakenExchange.marketDataService).then { krakenMarketDataService }
  }

  @AfterEach
  fun after() {
    ohlCsRepository.deleteAll()
  }

  @Test
  fun getTrades() {
    whenever(krakenMarketDataService.getTrades(eq(CurrencyPair.BTC_EUR), anyOrNull())).then {
      readTradesFromFiles(CurrencyPair.BTC_EUR)
    }
    val trades = krakenService.getTrades(CurrencyPair.BTC_EUR, 1104541200)
    assertThat(trades).isNotEmpty.size().isEqualTo(100)
  }

  @Test
  fun getPrice() {
    whenever(krakenMarketDataService.getTicker(eq(CurrencyPair.BTC_EUR))).then { ticker(CurrencyPair.BTC_EUR) }
    val price = krakenService.getPrice(CurrencyPair.BTC_EUR)
    assertThat(price).isNotNull
    assertThat(price.amount).isEqualTo(BigDecimal.valueOf(100.11))
    assertThat(price.currency.currencyCode).isEqualTo("EUR")
  }

  @Test
  fun retrieveTrades() {
    whenever(krakenMarketDataService.getTrades(eq(CurrencyPair.BTC_EUR), anyOrNull())).then {
      readTradesFromFiles(CurrencyPair.BTC_EUR)
    }
    whenever(krakenMarketDataService.getTrades(eq(CurrencyPair.ETH_EUR), anyOrNull())).then {
      readTradesFromFiles(CurrencyPair.ETH_EUR)
    }
    whenever(krakenExchange.exchangeSymbols).then { listOf(CurrencyPair.BTC_EUR, CurrencyPair.ETH_EUR, CurrencyPair.ADA_BNB) }
    whenever(krakenExchange.exchangeSpecification).then { ExchangeSpecification(KrakenExchange::class.java).also { it.exchangeName = "Kraken" } }
    krakenService.retrieveTrades()
    logger.info { "\n\nstart checks\n\n" }
    assertThat(ohlCsRepository.findByPairAndInterval(CurrencyPair.ADA_BNB.toString(), 1)).isEmpty()
    assertThat(ohlCsRepository.findByPairAndInterval(CurrencyPair.BTC_EUR.toString(), 1)).isNotEmpty
    assertThat(ohlCsRepository.findByPairAndInterval(CurrencyPair.BTC_EUR.toString(), 5)).isNotEmpty
    assertThat(ohlCsRepository.findByPairAndInterval(CurrencyPair.BTC_EUR.toString(), 15)).isNotEmpty
    assertThat(ohlCsRepository.findByPairAndInterval(CurrencyPair.BTC_EUR.toString(), 30)).isNotEmpty
    assertThat(ohlCsRepository.findByPairAndInterval(CurrencyPair.ETH_EUR.toString(), 1)).isNotEmpty
    assertThat(ohlCsRepository.findByPairAndInterval(CurrencyPair.ETH_EUR.toString(), 5)).isNotEmpty
    assertThat(ohlCsRepository.findByPairAndInterval(CurrencyPair.ETH_EUR.toString(), 15)).isNotEmpty
    assertThat(ohlCsRepository.findByPairAndInterval(CurrencyPair.ETH_EUR.toString(), 30)).isNotEmpty
  }

  private fun readTradesFromFiles(pair: CurrencyPair): Trades =
    readFromFileAsMap("kraken/$pair/trades.json")
      .let {
        val map = it as Map<*, *>
        val lastID = map["lastID"] as Long
        val sortType = map["tradeSortType"].let { type -> Trades.TradeSortType.valueOf(type as String) }
        val trades = (map["trades"] as List<*>).map { element ->
          val value = element as Map<*, *>
          Trade(
            Order.OrderType.valueOf(value["type"] as String),
            value["originalAmount"].let { v -> BigDecimal.valueOf(v as Double) },
            value["instrument"].let { v -> CurrencyPair(v as String) },
            value["price"].let { v -> BigDecimal.valueOf(v as Double) },
            value["timestamp"].let { v -> Date(v as Long) },
            value["id"] as String,
            null,
            null
          )
        }
        return@let Trades(trades, lastID, sortType)
      }


}
