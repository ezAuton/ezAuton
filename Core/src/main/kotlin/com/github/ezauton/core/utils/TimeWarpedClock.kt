package com.github.ezauton.core.utils

import com.github.ezauton.core.Duration
import com.github.ezauton.core.now
import kotlinx.coroutines.delay

/**
 * A clock based off of [RealClock] but is warped
 */
class TimeWarpedClock @JvmOverloads constructor(val speed: Double, private val startTime: Duration = now()) : Clock {

    override suspend fun delayFor(duration: Duration) = delay((duration.millis / speed).toLong())

    private val realClock: RealClock = RealClock.CLOCK
    private val timeStartedAt = now()

    override val time: Duration
        get() {
            val realDt = realClock.time - timeStartedAt
            return (realDt * speed + startTime)
        }
}
