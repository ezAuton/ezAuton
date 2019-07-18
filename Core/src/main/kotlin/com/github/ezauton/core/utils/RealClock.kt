package com.github.ezauton.core.utils

import com.github.ezauton.conversion.Time
import com.github.ezauton.conversion.now
import kotlinx.coroutines.delay

/**
 * A clock which represents the real world time. ⏱
 */
class RealClock private constructor() : Clock {

    override suspend fun delayFor(duration: Time) {
        delay(duration.millisL)
    }

    override val time get() = now()

    companion object {
        val CLOCK = RealClock()
    }
}
