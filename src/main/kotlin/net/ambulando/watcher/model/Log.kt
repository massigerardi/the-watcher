package net.ambulando.watcher.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "LOG")
data class Log(
  @Id
  val id: String? = null,
  val exchange: String,
  val timestamp: Long,
  val pair: String,
  val message: String,
  val isError: Boolean = false,
  val type: Type
)

enum class Type {
  TRADE,
  OHLC
}
