package net.ambulando.watcher

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

object TimeUtils {
    
    
    /*
    1378856820
    1378856831546
     */
    fun normalizeTime(time: Long): Long {
       val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
