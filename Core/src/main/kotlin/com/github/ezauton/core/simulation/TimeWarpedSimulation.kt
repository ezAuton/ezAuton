package com.github.ezauton.core.simulation

import com.github.ezauton.core.action.Action
import com.github.ezauton.core.utils.TimeWarpedClock
import java.util.ArrayList
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * A simulator which allows to run in real-time or real-time*{multiplier} 🔥
 *
 */
@Deprecated("Use {@link ActionScheduler}")
class TimeWarpedSimulation @JvmOverloads constructor(val speed: Double = 1.0) : Simulation {
    override val clock: TimeWarpedClock

    private val actions = ArrayList<Action>()

    init {
        clock = TimeWarpedClock(speed)
    }

    fun add(action: Action): TimeWarpedSimulation {
        actions.add(action)
        return this
    }

    /**
     * Run your simulation and blocks until done
     *
     * @param timeout The amoount of **real** time that you want your simulation to cap out at.
     * @param timeUnit The timeunit that the timeout is in
     */
    @Throws(TimeoutException::class, ExecutionException::class)
    override fun runSimulation(timeout: Long, timeUnit: TimeUnit) {
        val mainActionScheduler = MainActionScheduler(clock)
        val futures = ArrayList<Future<Void>>()
        for (action in actions) {
            val future = mainActionScheduler.scheduleAction(action)
            futures.add(future)
        }
        for (future in futures) {
            try {
                future.get(timeout, timeUnit)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
}
