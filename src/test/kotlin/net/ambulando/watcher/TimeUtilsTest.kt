package net.ambulando.watcher

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class TimeUtilsTest {

    @Test
    fun normalizeTime() {
        assertThat(TimeUtils.normalizeTime(1378856831546)).isEqualTo(1378856820)
    }
}
