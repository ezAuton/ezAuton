package com.github.ezauton.core.action

import com.github.ezauton.core.Duration
import kotlinx.coroutines.delay

/**
 * An action which ⏰ a certain amount of time before executing 1
 */
class DelayedAction(waitDuration: Duration) : BaseAction() {

    private var runnable: Runnable? = null
    private val millis: Long = waitDuration.millis

    constructor(duration: Duration, runnable: Runnable) : this(duration) {
        this.runnable = runnable
    }

    /**
     * Called when the time is up, i.e., the delay is done
     */
    private fun onTimeUp() {
        runnable?.run()
    }

    override suspend fun run(actionRunInfo: ActionRunInfo) {
        try {
            delay(millis)
        } catch (e: InterruptedException) {
            return
        }

        onTimeUp()
    }
}
