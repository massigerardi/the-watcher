package net.ambulando.watcher

import java.util.Calendar

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
    return calendar.timeInMillis/1000
  }
}
