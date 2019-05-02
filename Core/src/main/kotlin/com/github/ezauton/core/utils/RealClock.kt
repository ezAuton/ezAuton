package com.github.ezauton.core.utils

import kotlinx.coroutines.delay

/**
 * A clock which represents the real world time. ⏱
 */
class RealClock private constructor() : Clock {

    override suspend fun delayFor(duration: Duration) {
        delay(duration.millis)
    }

    override val time get() = now()

    companion object {
        val CLOCK = RealClock()
    }
}
