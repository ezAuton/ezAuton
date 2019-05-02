package com.github.ezauton.core.action

import com.github.ezauton.core.utils.units.Duration

/**
 * Describes an action that ends after a certain amount of time has elapsed
 */
abstract class TimedPeriodicAction
/**
 * @param period       How long to (repeatedly) run the runnables for
 * @param periodUnit
 * @param waitDuration     The timeunit that period is
 * @param durationUnit
 */(period: Duration = DEFAULT_PERIOD, private val waitDuration: Duration, resourceManagement: ResourceManagement = DEFAULT_RESOURCE_MANAGEMENT, vararg resourcePriorities: ResourcePriority) : PeriodicAction(period, resourceManagement, *resourcePriorities) {

    override fun isFinished(): Boolean {
        return this.stopwatch.read() > waitDuration
    }

}
