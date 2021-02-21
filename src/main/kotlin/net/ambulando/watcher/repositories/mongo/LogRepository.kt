package net.ambulando.watcher.repositories.mongo

import net.ambulando.watcher.model.Log
import net.ambulando.watcher.model.Type
import org.springframework.data.repository.CrudRepository

interface LogRepository : CrudRepository<Log, String> {

  fun findTopByPairAndExchangeAndIsErrorAndTypeOrderByTimestampDesc(pair: String, exchange: String, isError: Boolean = false, type: Type): Log?
}
