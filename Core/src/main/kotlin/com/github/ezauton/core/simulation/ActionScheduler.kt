package com.github.ezauton.core.simulation

import com.github.ezauton.core.action.Action

import java.util.concurrent.Future

/**
 * An interface which is used to schedule an action in a certain way. Nice for simulations.
 */
interface ActionScheduler {
    /**
     * @param action
     * @return the action
     */
    fun scheduleAction(action: Action): Future<Void>
}
