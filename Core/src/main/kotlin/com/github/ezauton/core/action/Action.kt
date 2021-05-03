package com.github.ezauton.core.action

import com.github.ezauton.conversion.Time
import com.github.ezauton.core.simulation.SimpleContext
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
typealias ActionFunc<T> = suspend ActionContext.() -> T
typealias SendActionFunc<T> = suspend SendActionContext<T>.() -> Unit

private suspend fun <T> actionContext(block: ActionFunc<T>): T {
  return coroutineScope {
    val context = SimpleContext(this)
    with(context) {
      block()
    }
  }
}

private fun <T> sendActionContext(block: suspend SendActionContext<T>.() -> Unit) = flow {
  coroutineScope {
    val context = SimpleContext(this)
    val sendActionContext = SendActionContextImpl<T>(context, this@flow)
    with(sendActionContext) {
      block()
    }
  }
}

fun <T> action(block: ActionFunc<T>): Action<T> {
  return object : Action<T> {
    override suspend fun run(): T {
      return actionContext(block)
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

@OptIn(ExperimentalTypeInference::class)
fun <T> sendAction(@BuilderInference block: suspend SendActionContext<T>.() -> Unit): SendAction<T> {
  return sendActionContext(block)
}


suspend fun <T> withTimeout(time: Time, block: suspend CoroutineScope.() -> T) = kotlinx.coroutines.withTimeout(time.millisL, block)
