package com.github.ezauton.core.action

import com.github.ezauton.conversion.Time
import com.github.ezauton.conversion.Units
import com.github.ezauton.core.utils.Clock
import kotlinx.coroutines.CoroutineScope

interface ActionContext: CoroutineScope, ActionGroup  {


  suspend fun delay(millis: Long) {
//    delay(Units.ms(millis))
  }

  suspend fun delay(duration: Time) {
//    clock.delayFor(duration)
  }
}
