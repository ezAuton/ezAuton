package com.github.ezauton.core.utils

import com.github.ezauton.core.Duration
import com.github.ezauton.core.now
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * ⏰ A clock where the time is manually changed.
 */
@ExperimentalCoroutinesApi
class ManualClock : Clock {

    private val timeToRunnableMap = TreeMap<Duration, Queue<Continuation<Unit>>>()

    override suspend fun delayFor(duration: Duration) {
        return suspendCoroutine { cont ->
            val absoluteTime = duration + time
            val queue = timeToRunnableMap.getOrPut(absoluteTime) { LinkedList() }
            queue.add(cont)
        }
    }

    override
    var time: Duration = Duration.NONE
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
    fun init(time: Duration = now()) {
        this.time = time
    }

    /**
     * Add time in milliseconds
     *
     * @param dt duration
     * @return The new time
     */
    fun addTime(dt: Duration): Duration {
        time += dt
        return time
    }
}
