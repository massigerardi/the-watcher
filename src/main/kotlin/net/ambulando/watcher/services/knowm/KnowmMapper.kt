package net.ambulando.watcher.services.knowm

import net.ambulando.watcher.model.Constants.SCALE
import net.ambulando.watcher.model.Price
import net.ambulando.watcher.model.Trade
import org.knowm.xchange.dto.marketdata.Ticker
import org.springframework.stereotype.Component
import java.math.RoundingMode
import java.util.Currency

@Component
class KnowmMapper {

  fun toTrade(trade: org.knowm.xchange.dto.marketdata.Trade): Trade =
    Trade(time = trade.timestamp.time, price = trade.price.setScale(SCALE, RoundingMode.HALF_EVEN), volume = trade.originalAmount)

  fun toPrice(ticker: Ticker): Price = Price(ticker.last, Currency.getInstance("EUR"))
}
