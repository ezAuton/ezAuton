package com.github.ezauton.core.simulation

import com.github.ezauton.core.action.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*

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

suspend fun Action.run(){
  Scheduler.run(this)
}

interface ActionGroup {

  /**
   * Add a sequential Action to the actions that we will run
   *
   * @param runnable The Action to run
   * @return this
   */
  suspend fun <T> sequential(action: Action<T>): T

  /**
   * Add a daemonic Action to the actions that we will run. It will run in parallel with another action, except it wil end at the same time as the other action.
   *
   * @param action The Action to run
   * @return this
   */
  fun with(block: ActionFunc)
//  fun with(action: Action)


  /**
   * Add a parallel Action to the actions that we will run. It will run in parallel and will end in its own time.
   *
   * @param action The action to run
   * @return this
   */
  fun parallel(block: ActionFunc)
//  fun parallel(action: Action)

}


class SimpleContext(private val scope: CoroutineScope): ActionContext, CoroutineScope by scope {

  private val withJobs = ArrayList<Job>()

  override suspend fun sequential(action: Action) {
    with(action){
      run()
    }
    println("cancelling with jobs")
    withJobs.forEach { it.cancel() }
    println("cancelled with jobs")
  }

  override fun with(block: ActionFunc) {
    val action = block.toAction()
    with(action)
  }

  override fun with(action: Action) {
    val job = launch {
      with(action){
        run()
      }
    }
    withJobs.add(job)
  }

  override fun parallel(block: ActionFunc) {
    val action = block.toAction()
    parallel(action)
  }

  override fun parallel(action: Action) {
    launch {
      with(action){
        run()
      }
    }
  }

}
