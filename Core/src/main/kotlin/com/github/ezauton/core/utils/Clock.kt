package com.github.ezauton.core.utils

import com.github.ezauton.conversion.Time

/**
 * Describes a Clock. The clock can be real or simulated. The purpose of a clock is to support a [Stopwatch]
 */
interface Clock {
    /**
     * @return The current time as read by the clock in seconds
     */
    val time: Time

    suspend fun delayFor(duration: Time)
}

suspend fun Clock.delayUntil(timeUntil: Time) {
    val dt = timeUntil - time
    require(dt.isPositive) { "the duration must be non-negative" }
    if (dt.isZero) return
    delayFor(dt)
}
