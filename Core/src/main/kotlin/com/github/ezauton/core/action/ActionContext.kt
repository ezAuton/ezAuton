package com.github.ezauton.core.action

import com.github.ezauton.conversion.SIUnit
import com.github.ezauton.conversion.Time
import com.github.ezauton.conversion.Units
import com.github.ezauton.core.utils.Clock

data class ActionContext(val clock: Clock) {

    suspend fun delay(millis: Long) {
        delay(Units.ms(millis))
    }

    suspend fun delay(duration: SIUnit<Time>) {
        clock.delayFor(duration)
    }
}
