package com.github.ezauton.core.utils

import com.github.ezauton.core.utils.units.Duration

/**
 * Describes a Clock. The clock can be real or simulated. The purpose of a clock is to support a [Stopwatch]
 */
interface Clock {
    /**
     * @return The current time as read by the clock in seconds
     */
    val time: Duration

    suspend fun delayFor(duration: Duration)
}

suspend fun Clock.delayUntil(timeUntil: Duration){
    val dt = timeUntil - time
    require(dt >= Duration.NONE){"the duration must be non-negative"}
    if(dt == Duration.NONE) return
    delayFor(dt)
}
