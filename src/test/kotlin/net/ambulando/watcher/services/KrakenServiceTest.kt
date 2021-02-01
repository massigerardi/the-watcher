package net.ambulando.watcher.services

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File

@SpringBootTest
internal class KrakenServiceTest {

    @Autowired
    private lateinit var krakenService: KrakenService
    
    
    @Test
    fun getTrade() {
        val  trades = krakenService.getTrade("xxx", 1104541200)
        assertThat(trades).isNotEmpty
        ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(File("src/test/resources/kraken/trades_XBTEUR.json"), trades)
    }

    @Test
    fun getPrice() {
        val price = krakenService.getPrice("xxx")
        assertThat(price).isNotNull
    }

}
