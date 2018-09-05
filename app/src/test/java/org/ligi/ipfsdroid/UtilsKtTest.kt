package org.ligi.ipfsdroid

import org.junit.Test

import org.junit.Assert.*

/**
 * Created by WillowTree on 9/5/18.
 */
class UtilsKtTest {

    @Test
    fun `1 sec reads correctly`() {
        val millis = 1000L
        val s = getReadableTimeFromMillis(millis)
        assertEquals(s, "00:01")
    }

    @Test
    fun `59 sec reads correctly`() {
        val millis = 59*1000L
        val s = getReadableTimeFromMillis(millis)
        assertEquals(s, "00:59")
    }

    @Test
    fun `1 min reads correctly`() {
        val millis = 60*1000L
        val s = getReadableTimeFromMillis(millis)
        assertEquals(s, "01:00")
    }

    @Test
    fun `59 min reads correctly`() {
        val millis = 59*60*1000L
        val s = getReadableTimeFromMillis(millis)
        assertEquals(s, "59:00")
    }

    @Test
    fun `1 hour reads correctly`() {
        val millis = 60*60*1000L
        val s = getReadableTimeFromMillis(millis)
        assertEquals(s, "1:00:00")
    }

    @Test
    fun `1 hour 1 min 1 sec reads correctly`() {
        val millis = 60*60*1000L + 60*1000L + 1000L
        val s = getReadableTimeFromMillis(millis)
        assertEquals(s, "1:01:01")
    }

    @Test
    fun `3 hour 3 min 3 sec reads correctly`() {
        val millis = 3*60*60*1000L + 3*60*1000L + 3*1000L
        val s = getReadableTimeFromMillis(millis)
        assertEquals(s, "3:03:03")
    }

    @Test
    fun `1000 millis equals 1 sec`() {
        val millis = 1000L
        val (s, m, h) = getHoursMinSec(millis = millis)
        assertEquals(h, 0)
        assertEquals(m, 0)
        assertEquals(s, 1)
    }

    @Test
    fun `60000 millis equals 1 min`() {
        val millis = 60*1000L
        val (s, m, h) = getHoursMinSec(millis = millis)
        assertEquals(h, 0)
        assertEquals(m, 1)
        assertEquals(s, 0)
    }

    @Test
    fun `3600000 millis equals 1 hour`() {
        val millis = 60*60*1000L
        val (s, m, h) = getHoursMinSec(millis = millis)
        assertEquals(h, 1)
        assertEquals(m, 0)
        assertEquals(s, 0)
    }

    @Test
    fun `3661000 millis equals 1 hour, 1 min, 1 sec`() {
        val millis = 60*60*1000L + 60*1000L + 1000L
        val (s, m, h) = getHoursMinSec(millis = millis)
        assertEquals(h, 1)
        assertEquals(m, 1)
        assertEquals(s, 1)
    }
}