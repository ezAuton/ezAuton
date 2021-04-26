package com.github.ezauton.core.simulation

import com.github.ezauton.core.action.Action
import kotlinx.coroutines.Deferred

/**
 * An interface which is used to schedule an action in a certain way. Nice for simulations.
 */
interface ActionScheduler {
  /**
   * @param action
   * @return the action
   */
  fun <T> scheduleAction(action: Action<T>): Deferred<T>
}
