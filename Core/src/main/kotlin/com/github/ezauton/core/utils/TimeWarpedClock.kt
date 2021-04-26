package com.github.ezauton.core.utils

import com.github.ezauton.conversion.Time
import com.github.ezauton.conversion.now
import kotlinx.coroutines.delay

/**
 * A clock based off of [RealClock] but is warped
 */
class TimeWarpedClock @JvmOverloads constructor(val speed: Double, private val startTime: Time = now()) : Clock {

  override suspend fun delayFor(duration: Time) = delay((duration.millis / speed).toLong())

  private val realClock: RealClock = RealClock.CLOCK
  private val timeStartedAt = now()

  override val time: Time
    get() {
      val realDt = realClock.time - timeStartedAt
      return (realDt * speed + startTime)
    }
}
