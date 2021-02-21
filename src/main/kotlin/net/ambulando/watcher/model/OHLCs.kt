package net.ambulando.watcher.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "CANDLES")
data class OHLCs(
  @Id
  val id: String? = null,

  @Indexed
  val interval: Int? = null,

  @Indexed
  val pair: String? = null,

  @Indexed
  val exchange: String? = null,

  @Indexed
  val ohlcs: List<OHLC>
)
