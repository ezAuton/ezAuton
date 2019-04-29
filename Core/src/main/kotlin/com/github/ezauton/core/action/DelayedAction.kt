package com.github.ezauton.core.action

import com.github.ezauton.core.Duration
import kotlinx.coroutines.delay

/**
 * An action which ⏰ a certain amount of time before executing 1
 */
abstract class DelayedAction(waitDuration: Duration) : Action {

    private val millis: Long = waitDuration.millis

    /**
     * Called when the time is up, i.e., the delay is done
     */
    abstract fun onTimeUp()

    override suspend fun run() {
        try {
            delay(millis)
        } catch (e: InterruptedException) {
            return
        }

        onTimeUp()
    }
}
