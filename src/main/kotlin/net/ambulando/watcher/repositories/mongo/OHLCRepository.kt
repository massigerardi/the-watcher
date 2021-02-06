package net.ambulando.watcher.repositories.mongo

import net.ambulando.watcher.model.OHLC
import net.ambulando.watcher.model.OHLCs
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OHLCRepository: MongoRepository<OHLC, UUID>{

    @Query(value = "{ 'interval': ?0, 'ohlcs.time': { \$gte: ?1, \$lte: ?2} }", fields = " {'ohlcs': 1}")
    fun findOHLCsByIntervalAndOHLCsTimeBetwee(interval: Int, from: Long, to: Long): OHLC
    
}
