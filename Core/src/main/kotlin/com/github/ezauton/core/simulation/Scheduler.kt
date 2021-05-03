package com.github.ezauton.core.simulation

import com.github.ezauton.conversion.Time
import com.github.ezauton.core.action.*
import com.github.ezauton.core.utils.RealClock
import com.github.ezauton.core.utils.Stopwatch
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import java.util.*


interface ActionGroup {


  suspend fun <T> sequential(action: Action<T>)
  suspend fun <T> sequentialSend(action: SendAction<T>)

  /**
   * Add a parallel Action to the actions that we will run. It will run in parallel and will end in its own time.
   *
   * @param action The action to run
   * @return this
   */
  fun <T> parallel(block: ActionFunc<T>)
  fun <T> parallel(action: Action<T>)

  /**
   * Add a parallel Action to the actions that we will run. It will run in parallel and will end in its own time.
   *
   * @param action The action to run
   * @return this
   */
  fun <T> parallelSend(block: SendActionFunc<T>)
  fun <T> parallelSend(action: SendAction<T>): Flow<T>

}

object WithCancel : CancellationException("with cancellation")

// TODO: move location
class SimpleContext(private val scope: CoroutineScope) : ActionContext, CoroutineScope by scope {

  private val withJobs = ArrayList<Job>()

  private val stopwatch = run {
    val s = Stopwatch(RealClock)
    s.init()
    s
  }
  override val timeSinceStart: Time get() = stopwatch.read()

  override suspend fun <T> sequential(action: Action<T>) {
    with(action) {
      run()
    }
    println("cancelling with jobs")
    withJobs.forEach {
      println("cancle job: $it")
      it.cancel(cause = WithCancel)
    }
    println("cancelled with jobs")
  }

  override suspend fun <T> sequentialSend(action: SendAction<T>) {
    TODO("Not yet implemented")
  }


  override fun <T> parallel(block: ActionFunc<T>) {
    val action = action(block)
    parallel(action)
  }

  override fun <T> parallel(action: Action<T>) {
    launch {
      with(action) {
        run()
      }
    }
  }

  override fun <T> parallelSend(block: SendActionFunc<T>) {
    TODO("Not yet implemented")
  }

  override fun <T> parallelSend(action: SendAction<T>): Flow<T> {
    return action.shareIn(scope, started = SharingStarted.Eagerly, replay = 1000)
  }

}
