package com.github.ezauton.core.action

import com.github.ezauton.core.Duration

/**
 * A background is an action that runs forever (until manually ended with [Action.end].
 */
class BackgroundAction
/**
 * @param period
 * @param runnables
 */
(period: Duration, vararg runnables: Runnable) : PeriodicAction(period, *runnables) {

    override fun isFinished() = false
}
