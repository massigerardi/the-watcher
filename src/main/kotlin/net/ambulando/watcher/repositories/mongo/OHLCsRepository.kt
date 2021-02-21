package net.ambulando.watcher.repositories.mongo

import net.ambulando.watcher.model.OHLCs
import org.springframework.data.mongodb.core.aggregation.AggregationResults
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

/*
db.getCollection('oHLCs').aggregate([
    { $match: { 'interval': 1440 } },
    { $unwind : '$ohlcs' },
    { $match: { 'interval': 1440, 'ohlcs.time': { $gte:1378771200000, $lte:1379721600000 }}},
    { $group : { '_id' : '$interval', 'candles': {$push: '$ohlcs'} } },
    { $project : { '_id' : 0, 'candles': 1 }}
])

 */


@Repository
interface OHLCsRepository : MongoRepository<OHLCs, UUID> {

  @Aggregation(
    pipeline = [
      "{ \$match: { 'interval': ?0, 'pair': ?1, 'exchange': ?2} }",
      "{ \$unwind : '\$ohlcs' }",
      "{ \$match: { 'ohlcs.time': { \$gte:?3, \$lte:?4 }}}",
      "{ \$group : { '_id' : '\$interval', 'interval' :  {\$avg: '\$interval'}, 'ohlcs': {\$push: '\$ohlcs'} } }",
      "{ \$project : { '_id' : 0, 'ohlcs': 1, 'interval' : 1}}"
    ]
  )
  fun findOHLCBetween(interval: Int, pair: String, exchange: String, from: Long, to: Long): OHLCs

  fun findByInterval(interval: Int): List<OHLCs?>

  fun findByPairAndInterval(pair: String, interval: Int): List<OHLCs?>
}
