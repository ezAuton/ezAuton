package com.github.ezauton.core.action

import com.github.ezauton.core.utils.units.Duration
import com.github.ezauton.core.action.require.Resource

/**
 * An action which ⏰ a certain amount of time before executing 1
 */
abstract class DelayedAction(waitDuration: Duration, vararg val resources: Resource) : Action {

    private val millis: Long = waitDuration.millis

    /**
     * Called when the time is up, i.e., the delay is done
     */
    abstract fun onTimeUp()

    override suspend fun ActionContext.run() {
        try {
            delay(millis)
        } catch (e: InterruptedException) {
            return
        }
        resources.map { it.take() }
        onTimeUp()
    }
}
