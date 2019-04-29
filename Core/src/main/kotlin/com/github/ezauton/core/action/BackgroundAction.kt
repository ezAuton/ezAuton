package com.github.ezauton.core.action

import com.github.ezauton.core.Duration
import com.github.ezauton.core.action.require.Resource

/**
 * A background is an action that runs forever (until manually ended with [Action.end].
 */
abstract class BackgroundAction
/**
 * @param period
 * @param runnables
 */
(period: Duration, vararg val resources: Resource) : Action {
    override suspend fun run() {

    }
}
