package com.github.ezauton.core.simulation

import com.github.ezauton.conversion.Time
import com.github.ezauton.core.action.*
import com.github.ezauton.core.utils.RealClock
import com.github.ezauton.core.utils.Stopwatch
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.*
import java.util.*

///**
// * An interface which is used to schedule an action in a certain way. Nice for simulations.
// */
//interface Scheduler {
//  /**
//   * @param action
//   * @return the action
//   */
//  suspend fun run(action: Action)
//  fun CoroutineScope.runJob(action: Action): Job
//
//  companion object Default : Scheduler {
//    override suspend fun run(action: Action) = coroutineScope {
//      val context = SimpleContext(this)
//      with(action) {
//        context.run()
//      }
//    }
//
//    override fun CoroutineScope.runJob(action: Action) = launch {
//      val context = SimpleContext(this)
//      with(action) {
//        context.run()
//      }
//    }
//  }
//
//}

//suspend fun Action.run(){
//  AbstractScheduledService.Scheduler.run(this)
//}


interface ActionGroup {


  suspend fun <T> sequential(action: Action<T>)
  suspend fun <T> sequentialSend(action: SendAction<T>)

  /**
   * Add a daemonic Action to the actions that we will run. It will run in parallel with another action, except it wil end at the same time as the other action.
   *
   * @param action The Action to run
   * @return this
   */
  fun <T> with(block: ActionFunc<T>)
  fun <T> with(action: Action<T>)

  fun <T> sendWith(action: SendAction<T>): Flow<T>
  fun <T> sendWith(action: SendActionFunc<T>): Flow<T>


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

  override suspend fun sequential(action: Action) {
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

  override fun with(block: ActionFunc) {
    val action = action(block)
    with(action)
  }

  override fun with(action: Action) {
    val job = launch {
      try {
        action.run()
      } catch (e: WithCancel) {
        // ignore
      }
    }
    withJobs.add(job)
  }

  @OptIn(ExperimentalCoroutinesApi::class, kotlinx.coroutines.FlowPreview::class)
  override fun <T> sendWith(action: SendAction<T>): Flow<T> {

    val broadcastChannel = BroadcastChannel<T>(capacity = 1000) // TODO: figure out unlimited somehow

    val job = launch {
      try {
        action.collect {
          broadcastChannel.send(it)
        }
      } catch (e: WithCancel) {
        println("disregard!!!")
      }
    }

    withJobs.add(job)

    return broadcastChannel.asFlow()
  }

  override fun <T> sendWith(action: SendActionFunc<T>): Flow<T> {
    TODO("Not yet implemented")
  }

  override fun parallel(block: ActionFunc) {
    val action = action(block)
    parallel(action)
  }

  override fun parallel(action: Action) {
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
