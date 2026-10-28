package com.pbogdev.core.utils

import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant


/**
 * Returns the exact [Instant] 7 days from the current moment.
 */
fun epochMillisToIso(epochMillis: Long): String {
    return Instant.fromEpochMilliseconds(epochMillis).toString()
}

fun getSevenDaysFromNow(): Instant {
    return Clock.System.now() + 7.days
}

/**
 * Returns an ISO 8601 formatted string of the time 7 days from now.
 * Drops trailing zeros if milliseconds are flat (e.g., "2026-07-04T08:11:53Z").
 */
fun getSevenDaysFromNowIso(): String {
    return getSevenDaysFromNow().toString()
}



