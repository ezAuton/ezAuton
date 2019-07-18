package com.github.ezauton.core.utils

import com.github.ezauton.conversion.Time
import com.github.ezauton.conversion.seconds

/**
 * A handy stopwatch for recording time in seconds since it was last polled. Requires a ⏱ [Clock] to keep track
 * of time.
 */
class Stopwatch(val clock: Clock) {
    private var startDuration: Time = (-1).seconds

    /**
     * @return If this stopwatch is initialized
     */
    val isInit: Boolean get() = startDuration.value >= 0.0

    fun init() {
        startDuration = clock.time
    }

    /**
     * Read and reset
     *
     * @return The value of the stopwatch (ms)
     */
    fun pop(): Time {
        val readVal = read()
        reset()
        return readVal
    }

    /**
     * Read without resetting
     *
     * @return The value of the stopwatch (ms)
     */
    fun read(): Time {
        if (!isInit) throw IllegalArgumentException("Stopwatch must be initialized to use")
        return clock.time - startDuration
    }

    /**
     * Reset without reading
     */
    fun reset(): Stopwatch {
        startDuration = clock.time
        return this
    }

    /**
     * @return If is not init
     */
    fun resetIfNotInit(): Boolean {
        if (isInit) {
            return false
        }
        reset()
        return true
    }
}
