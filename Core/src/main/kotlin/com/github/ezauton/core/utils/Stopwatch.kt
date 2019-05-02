package com.github.ezauton.core.utils

import com.github.ezauton.conversion.Duration

/**
 * A handy stopwatch for recording time in seconds since it was last polled. Requires a ⏱ [Clock] to keep track
 * of time.
 */
class Stopwatch(val clock: Clock) {
    private lateinit var startDuration: Duration

    /**
     * @return If this stopwatch is initialized
     */
    val isInit: Boolean get() = ::startDuration.isInitialized

    fun init() {
        startDuration = clock.time
    }

    /**
     * Read and reset
     *
     * @return The value of the stopwatch (ms)
     */
    fun pop(): Duration {
        val readVal = read()
        reset()
        return readVal
    }

    /**
     * Read without resetting
     *
     * @return The value of the stopwatch (ms)
     */
    fun read(): Duration {
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
