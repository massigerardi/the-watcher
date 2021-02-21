package net.ambulando.watcher.repositories.mongo

import net.ambulando.watcher.model.Log
import net.ambulando.watcher.model.Type
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
@AutoConfigureDataMongo
internal class LogRepositoryTest {
    
    @Autowired
    private lateinit var repository: LogRepository
    
    companion object {
        const val timestamp = 1383264000000
        const val ten_minutes = 3600000
    }
    
    @BeforeEach
    fun init() {
        repository.save(Log(id = UUID.randomUUID().toString(), pair = "XBTEUR", timestamp = timestamp, message = "save 1", exchange = "test_exchange", type = Type.TRADE))
        repository.save(Log(id = UUID.randomUUID().toString(), pair = "XBTEUR", timestamp = timestamp + ten_minutes, message = "save 1", exchange = "test_exchange", type = Type.TRADE))
        repository.save(Log(id = UUID.randomUUID().toString(), pair = "XBTEUR", timestamp = timestamp + ten_minutes * 2, message = "save 1", exchange = "test_exchange", type = Type.TRADE))
        repository.save(Log(id = UUID.randomUUID().toString(), pair = "XBTEUR", timestamp = timestamp + ten_minutes * 3, message = "save 1", exchange = "test_exchange", type = Type.TRADE))
        repository.save(Log(id = UUID.randomUUID().toString(), pair = "XBTEUR", timestamp = timestamp + ten_minutes * 4, message = "error", exchange = "test_exchange", isError = true, type = Type.TRADE))

        repository.save(Log(id = UUID.randomUUID().toString(), pair = "XBTEUR", timestamp = timestamp, message = "save 1", exchange = "test_exchange", type = Type.OHLC))
        repository.save(Log(id = UUID.randomUUID().toString(), pair = "XBTEUR", timestamp = timestamp + ten_minutes, message = "save 1", exchange = "test_exchange", type = Type.OHLC))
        repository.save(Log(id = UUID.randomUUID().toString(), pair = "XBTEUR", timestamp = timestamp + ten_minutes * 2, message = "save 1", exchange = "test_exchange", type = Type.OHLC))
        repository.save(Log(id = UUID.randomUUID().toString(), pair = "XBTEUR", timestamp = timestamp + ten_minutes * 3, message = "save 1", exchange = "test_exchange", type = Type.OHLC))

        repository.save(Log(id = UUID.randomUUID().toString(), pair = "XBTEUR", timestamp = timestamp, message = "save 1", exchange = "test_exchange_2", type = Type.TRADE))
        repository.save(Log(id = UUID.randomUUID().toString(), pair = "XBTEUR", timestamp = timestamp + ten_minutes, message = "save 1", exchange = "test_exchange_2", type = Type.TRADE))
        repository.save(Log(id = UUID.randomUUID().toString(), pair = "XBTEUR", timestamp = timestamp + ten_minutes * 2, message = "save 1", exchange = "test_exchange_2", type = Type.TRADE))
        repository.save(Log(id = UUID.randomUUID().toString(), pair = "XBTEUR", timestamp = timestamp + ten_minutes * 3, message = "save 1", exchange = "test_exchange_2", type = Type.TRADE))
        
        repository.save(Log(id = UUID.randomUUID().toString(), pair = "ETHEUR", timestamp = timestamp, message = "save 1", exchange = "test_exchange", type = Type.TRADE))
        repository.save(Log(id = UUID.randomUUID().toString(), pair = "ETHEUR", timestamp = timestamp + ten_minutes, message = "save 1", exchange = "test_exchange", type = Type.TRADE))
        repository.save(Log(id = UUID.randomUUID().toString(), pair = "ETHEUR", timestamp = timestamp + ten_minutes * 2, message = "save 1", exchange = "test_exchange", type = Type.TRADE))
        repository.save(Log(id = UUID.randomUUID().toString(), pair = "ETHEUR", timestamp = timestamp + ten_minutes * 3, message = "save 1", exchange = "test_exchange", type = Type.TRADE))
    }
    
    @AfterEach
    fun reset() {
        repository.deleteAll()
    }

    @Test
    fun `should return last log entry without errors`() {
        val log = repository.findTopByPairAndExchangeAndIsErrorAndTypeOrderByTimestampDesc("XBTEUR", "test_exchange", type = Type.TRADE)
        assertThat(log).isNotNull
        assertThat(log?.timestamp).isEqualTo(timestamp + ten_minutes * 3)
        assertThat(log?.pair).isEqualTo("XBTEUR")
    }

    @Test
    fun `should return last log entry with errors`() {
        val log = repository.findTopByPairAndExchangeAndIsErrorAndTypeOrderByTimestampDesc("XBTEUR", "test_exchange", true, Type.TRADE)
        assertThat(log).isNotNull
        assertThat(log?.timestamp).isEqualTo(timestamp + ten_minutes * 4)
        assertThat(log?.pair).isEqualTo("XBTEUR")
        assertThat(log?.message).isEqualTo("error")
    }

    @Test
    fun `should return last log entry for OHLC`() {
        val log = repository.findTopByPairAndExchangeAndIsErrorAndTypeOrderByTimestampDesc("XBTEUR", "test_exchange", false, Type.OHLC)
        assertThat(log).isNotNull
        assertThat(log?.timestamp).isEqualTo(timestamp + ten_minutes * 3)
        assertThat(log?.pair).isEqualTo("XBTEUR")
    }

}
