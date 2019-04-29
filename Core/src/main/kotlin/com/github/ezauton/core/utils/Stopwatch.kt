package com.github.ezauton.core.utils

import java.util.concurrent.TimeUnit

/**
 * A handy stopwatch for recording time in seconds since it was last polled. Requires a ⏱ [Clock] to keep track
 * of time.
 */
class Stopwatch(val clock: Clock) {
    protected var millis: Long = -1

    /**
     * @return If this stopwatch is initialized
     */
    val isInit: Boolean
        get() = millis != -1

    fun init() {
        millis = clock.time

    }

    /**
     * Read and reset
     *
     * @return The value of the stopwatch (ms)
     */
    fun pop(): Double {
        val readVal = read().toDouble()
        reset()
        return readVal
    }

    /**
     * Read and reset
     *
     * @param timeUnit The time unit you would like to get the result in
     * @return Value of stopwatch (in specified timeunit)
     */
    fun pop(timeUnit: TimeUnit): Double {
        return pop() / timeUnit.toMillis(1)
    }

    /**
     * Read without resetting
     *
     * @return The value of the stopwatch (ms)
     */
    fun read(): Long {
        if (!isInit) throw IllegalArgumentException("Stopwatch must be initialized to use")
        return clock.time - millis
    }

    fun read(timeUnit: TimeUnit): Long {
        return timeUnit.convert(read(), TimeUnit.MILLISECONDS)
    }

    /**
     * Reset without reading
     */
    fun reset(): Stopwatch {
        millis = clock.time
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
