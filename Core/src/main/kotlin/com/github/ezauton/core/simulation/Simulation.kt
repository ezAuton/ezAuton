package com.github.ezauton.core.simulation

import com.github.ezauton.core.utils.Clock

import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * A simulator for actions
 */
@Deprecated("Use {@link ActionScheduler}")
interface Simulation {

    val clock: Clock

    /**
     * Uses real units for timeout, not simulated
     *
     * @param timeout
     * @param timeUnit
     * @throws TimeoutException
     */
    @Throws(TimeoutException::class, ExecutionException::class)
    fun runSimulation(timeout: Long, timeUnit: TimeUnit)
}
