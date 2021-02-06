package net.ambulando.watcher.repositories.mongo

import net.ambulando.watcher.services.OHLCService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class OHLCsRepositoryTest {
    
    @Autowired
    private lateinit var repository: OHLCsRepository
    
    @Autowired
    private lateinit var service: OHLCService
    
    @BeforeEach
    fun init() {
        println("init")
        service.loadOHLC(1,"src/test/resources/kraken/XBTEUR_1.csv")
        service.loadOHLC(1440,"src/test/resources/kraken/XBTEUR_1440_A.csv")
        service.loadOHLC(1440,"src/test/resources/kraken/XBTEUR_1440_B.csv")
    }
    
    
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
    fun `should return candles between tow timestamps`() {
        val ohlcs = repository.findOHLCsByIntervalAndOHLCsTimeBetwee(1440, 1378771200, 1379721600)
        assertThat(ohlcs).isNotNull
        assertThat(ohlcs.ohlcs).isNotEmpty
        
    }
}
