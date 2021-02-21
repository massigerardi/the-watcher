package net.ambulando.watcher.repositories.mongo

import net.ambulando.watcher.services.OHLCService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import java.io.File

@SpringBootTest
@AutoConfigureDataMongo
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
internal class OHLCsRepositoryTest {

  @Autowired
  private lateinit var repository: OHLCsRepository

  @Autowired
  private lateinit var service: OHLCService

  @BeforeEach
  fun init() {
    println("init")
    service.loadFromCSV(1, File("src/test/resources/kraken/XBTEUR_1.csv"), "XBTEUR", "test")
    service.loadFromCSV(1440, File("src/test/resources/kraken/XBTEUR_1440_A.csv"), "XBTEUR", "test")
    service.loadFromCSV(1440, File("src/test/resources/kraken/XBTEUR_1440_B.csv"), "XBTEUR", "test")
  }

  @AfterEach
  fun reset() {
    println("reset")
    repository.deleteAll()
  }

  @Test
  fun `should return all collections`() {
    val all = repository.findAll()
    assertThat(all).isNotEmpty
    assertThat(all.size).isEqualTo(3)
  }

  @Test
  fun `should return all collections within an interval`() {
    val all = repository.findByInterval(1440)
    assertThat(all).isNotEmpty
    assertThat(all.size).isEqualTo(2)
  }

  @Test
  fun `should return candles between two timestamps`() {
    val ohlcs = repository.findOHLCBetween(1440, "XBTEUR", "test", 1378771200000, 1379721600000)
    assertThat(ohlcs).isNotNull
    assertThat(ohlcs.ohlcs).isNotEmpty
    assertThat(ohlcs.ohlcs.size).isEqualTo(10)
  }

  @Test
  fun `should return candles between two timestamps in different records`() {
    val ohlcs = repository.findOHLCBetween(1440, "XBTEUR", "test", 1383264000000, 1383523200000)
    assertThat(ohlcs).isNotNull
    assertThat(ohlcs.ohlcs).isNotEmpty
    assertThat(ohlcs.ohlcs.size).isEqualTo(4)
  }

}
