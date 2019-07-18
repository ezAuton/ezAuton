package com.github.ezauton.core.action

import com.github.ezauton.conversion.Time
import com.github.ezauton.core.action.require.Resource

/**
 * An action which ⏰ a certain amount of time before executing 1
 */
abstract class DelayedAction(waitDuration: Time, vararg val resources: Resource) : Action {

    private val waitMillis: Long = waitDuration.millis.toLong()

    /**
     * Called when the time is up, i.e., the delay is done
     */
    abstract fun onTimeUp()

    override suspend fun ActionContext.run() {
        try {
            delay(waitMillis)
        } catch (e: InterruptedException) {
            return
        }
        resources.map { it.take() }
        onTimeUp()
    }
}
