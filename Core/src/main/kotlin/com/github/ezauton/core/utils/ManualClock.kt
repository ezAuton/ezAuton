package com.github.ezauton.core.utils

import com.github.ezauton.conversion.Time
import com.github.ezauton.conversion.now
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.LinkedList
import java.util.Queue
import java.util.TreeMap
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * ⏰ A clock where the time is manually changed.
 */
@ExperimentalCoroutinesApi
class ManualClock : Clock {

    private val timeToRunnableMap = TreeMap<Time, Queue<Continuation<Unit>>>()

    override suspend fun delayFor(duration: Time) {
        return suspendCoroutine { cont ->
            val absoluteTime = duration + time
            val queue = timeToRunnableMap.getOrPut(absoluteTime) { LinkedList() }
            queue.add(cont)
        }
    }

    override
    var time: Time = Time.NONE
        set(time) {
            while (!timeToRunnableMap.isEmpty() && timeToRunnableMap.firstKey() <= time) {
                val entry = timeToRunnableMap.pollFirstEntry()
                val queue = entry.value
                queue.removeIf { cont ->
                    cont.resume(Unit)
                    true
                }
            }
            field = time
        }

    @JvmOverloads
    fun init(time: Time = now()) {
        this.time = time
    }

    /**
     * Add time in milliseconds
     *
     * @param dt duration
     * @return The new time
     */
    fun addTime(dt: Time): Time {
        time += dt
        return time
    }
}
