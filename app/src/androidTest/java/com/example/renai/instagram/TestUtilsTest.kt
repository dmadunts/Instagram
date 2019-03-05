package com.example.renai.instagram

import android.support.test.runner.AndroidJUnit4
import com.example.renai.instagram.common.formatRelativeTimestamp
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class TestUtilsTest {
    private val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, 2019)
        set(Calendar.MONTH, 0)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }

    @Test
    fun shouldFormatRelativeTime() {
        val time = calendar.time
        val time10sec = calendar.change { add(Calendar.SECOND, 10) }.time
        val time10min = calendar.change { add(Calendar.MINUTE, 10) }.time
        assertEquals("10 sec", formatRelativeTimestamp(time, time10sec))
        assertEquals("10 min", formatRelativeTimestamp(time, time10min))
    }

    private fun Calendar.change(f: Calendar.() -> Unit): Calendar {
        val newCalendar = clone() as Calendar
        f(newCalendar)
        return newCalendar
    }
}
