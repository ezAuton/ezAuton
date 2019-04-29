package com.github.ezauton.core.action

import com.github.ezauton.core.Duration
import java.util.concurrent.TimeUnit

/**
 * Describes an action that ends after a certain amount of time has elapsed
 */
class TimedPeriodicAction : PeriodicAction {

    private var durationMillis: Long = 0

    /**
     * @param period       How long to (repeatedly) run the runnables for
     * @param periodUnit
     * @param duration     The timeunit that period is
     * @param durationUnit
     */
    constructor(period: Duration = DEFAULT_PERIOD, duration: Duration , vararg runnables: Runnable) : super(period, *runnables) {
        durationMillis = duration.millis
    }

    /**
     * @param duration     The timeunit that period is
     * @param durationUnit
     */
    constructor(duration: Duration, vararg runnables: Runnable) : super(*runnables) {
        durationMillis = duration.millis
    }

    /**
     * @param duration     The timeunit that period is
     * @param durationUnit
     */
    constructor(duration: Long, durationUnit: TimeUnit) : super() {
        durationMillis = durationUnit.toMillis(duration)
    }

    override fun isFinished(): Boolean {
        return this.stopwatch.read() > durationMillis
    }

}
