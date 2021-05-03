package com.github.ezauton.core.action

import com.github.ezauton.conversion.Time
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlin.experimental.ExperimentalTypeInference


/**
 * Describes an Action, which is similar to a WPILib Commands, but has both linear, periodic, and other implementations.
 * Additionally, it is not bound to the 20ms periodic timer for WPILib Commands. 👋 Commands! 🚀 🤖
 */
interface Action<T> : Flow<T> { // In the purest form an action is of type: suspend () -> Unit
  /**
   * Run the action given a clock 🏃‍️
   *
   */
  @Throws(Exception::class)
  suspend fun run(): T

  @InternalCoroutinesApi
  override suspend fun collect(collector: FlowCollector<T>) {
    collector.emit(run())
  }
}

/**
 * Describes an Action, which is similar to a WPILib Commands, but has both linear, periodic, and other implementations.
 * Additionally, it is not bound to the 20ms periodic timer for WPILib Commands. 👋 Commands! 🚀 🤖
 */
typealias SendAction<T> = Flow<T>
typealias ActionFunc<T> = suspend CoroutineScope.() -> T
typealias SendActionFunc<T> = suspend FlowCollector<T>.() -> Unit

fun <T> action(block: ActionFunc<T>): Action<T> {
  return object : Action<T> {
    override suspend fun run(): T = coroutineScope {
      block()
    }
  }
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> ephemeral(block: suspend CoroutineScope.() -> T): T {

  val job = Job()
  val scope = CoroutineScope(job)

  val result = with(scope) {
    block()
  }

  job.cancel()

  return result

}

suspend fun delay(duration: Time) {
  delay(duration.millisL)
}


@OptIn(ExperimentalTypeInference::class)
fun <T> sendAction(@BuilderInference block: SendActionFunc<T>)= flow<T> {
  block(this)
}


suspend fun <T> withTimeout(time: Time, block: suspend CoroutineScope.() -> T) = withTimeout(time.millisL, block)
