package com.github.ezauton.core.action

import com.github.ezauton.conversion.Time
import kotlinx.coroutines.CoroutineScope

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
  suspend fun ActionContext.run()
}

typealias ActionFunc = suspend ActionContext.() -> Unit

fun action(block: ActionFunc) = object : Action {
  override suspend fun ActionContext.run(){
    block()
  }
}

fun ActionFunc.toAction() = object : Action {
  override suspend fun ActionContext.run() {
    this@toAction()
  }
}


suspend fun <T> withTimeout(time: Time, block: suspend CoroutineScope.() -> T) = kotlinx.coroutines.withTimeout(time.millisL, block)

//
//fun Runnable.toAction() = object : Action {
//  override suspend fun ActionContext.run() {
//    this@toAction.run()
//  }
//}
