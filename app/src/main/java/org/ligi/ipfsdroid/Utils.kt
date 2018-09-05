package org.ligi.ipfsdroid

/**
 * Created by WillowTree on 9/5/18.
 */

const val MILLIS_IN_SEC = 1000
const val MILLIS_IN_MIN = MILLIS_IN_SEC * 60
const val MILLIS_IN_HOUR = MILLIS_IN_MIN *60

fun getReadableTimeFromMillis(millis: Long) : String {

    val (seconds, minutes, hours) = getHoursMinSec(millis)

    return when {
        hours >= 1 -> String.format("%01d:%02d:%02d", hours, minutes, seconds)
        else -> String.format("%02d:%02d", minutes, seconds)
    }
}

fun getHoursMinSec(millis: Long) : Triple<Long, Long, Long> {
    val seconds = (millis / MILLIS_IN_SEC) % 60
    val minutes = (millis / (MILLIS_IN_MIN)) % 60
    val hours = (millis / (MILLIS_IN_HOUR)) % 24
    return Triple(seconds, minutes, hours)
}