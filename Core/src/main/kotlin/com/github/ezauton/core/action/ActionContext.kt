package com.github.ezauton.core.action

import com.github.ezauton.conversion.Time
import com.github.ezauton.core.simulation.ActionGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.FlowCollector

interface ActionContext : CoroutineScope, ActionGroup {


  val timeSinceStart: Time

  suspend fun delay(millis: Long) {
//    delay(Units.ms(millis))
  }

  suspend fun delay(duration: Time) {
//    clock.delayFor(duration)
  }

}


interface SendActionContext<T> : ActionContext, FlowCollector<T>
class SendActionContextImpl<T>(private val actionContext: ActionContext, flowCollector: FlowCollector<T>) : SendActionContext<T>, ActionContext by actionContext, FlowCollector<T> by flowCollector
