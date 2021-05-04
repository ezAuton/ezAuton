package com.github.ezauton.core.simulation

import com.github.ezauton.core.action.Action
import com.github.ezauton.core.action.ActionFunc
import com.github.ezauton.core.action.action
import kotlinx.coroutines.*

fun <T> CoroutineScope.parallel(block: ActionFunc<T>): Deferred<T> {
  val action = action(block)
  return parallel(action)
}

fun CoroutineScope.parallel(block: ActionFunc<Unit>): Job {
  val action = action(block)
  return parallel(action)
}

fun CoroutineScope.parallel(action: Action<Unit>) = launch(start = CoroutineStart.DEFAULT) {
  action.run()
}

fun <T> CoroutineScope.parallel(action: Action<T>): Deferred<T> = async(start = CoroutineStart.DEFAULT) {
  action.run()
}

suspend fun <T> sequential(action: Action<T>): T = action.run()
