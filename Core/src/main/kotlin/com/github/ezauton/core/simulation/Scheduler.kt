package com.github.ezauton.core.simulation

import com.github.ezauton.core.action.Action
import com.github.ezauton.core.action.SimpleContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * An interface which is used to schedule an action in a certain way. Nice for simulations.
 */
interface Scheduler {
  /**
   * @param action
   * @return the action
   */
  suspend fun run(action: Action)
  fun CoroutineScope.runJob(action: Action): Job

  companion object Default : Scheduler {
    override suspend fun run(action: Action) = coroutineScope {
      val context = SimpleContext(this)
      with(action) {
        context.run()
      }
    }

    override fun CoroutineScope.runJob(action: Action) = launch {
      val context = SimpleContext(this)
      with(action) {
        context.run()
      }
    }
  }

}
