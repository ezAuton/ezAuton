package com.github.ezauton.core.utils

import com.github.ezauton.core.utils.units.Duration
import com.github.ezauton.core.utils.units.now
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
