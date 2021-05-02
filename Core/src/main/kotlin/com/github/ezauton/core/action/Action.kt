package com.github.ezauton.core.action

import com.github.ezauton.conversion.Time
import com.github.ezauton.core.simulation.SimpleContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.experimental.ExperimentalTypeInference

/**
 * Describes an Action, which is similar to a WPILib Commands, but has both linear, periodic, and other implementations.
 * Additionally, it is not bound to the 20ms periodic timer for WPILib Commands. 👋 Commands! 🚀 🤖
 */
interface Action { // In the purest form an action is of type: suspend () -> Unit
  /**
   * Run the action given a clock 🏃‍️
   *
   */
  @Throws(Exception::class)
  suspend fun run()
}

/**
 * Describes an Action, which is similar to a WPILib Commands, but has both linear, periodic, and other implementations.
 * Additionally, it is not bound to the 20ms periodic timer for WPILib Commands. 👋 Commands! 🚀 🤖
 */
typealias SendAction<T> = Flow<T>
typealias ActionFunc = suspend ActionContext.() -> Unit
typealias SendActionFunc<T> = suspend SendActionContext<T>.() -> Unit

private suspend fun actionContext(block: ActionFunc) {
  coroutineScope {
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

fun action(block: ActionFunc): Action {
  return object : Action {
    override suspend fun run() {
      actionContext(block)
    }
  }
}

@OptIn(ExperimentalTypeInference::class)
fun <T> sendAction(@BuilderInference block: suspend SendActionContext<T>.() -> Unit): SendAction<T> {
  return sendActionContext(block)
}



suspend fun <T> withTimeout(time: Time, block: suspend CoroutineScope.() -> T) = kotlinx.coroutines.withTimeout(time.millisL, block)
