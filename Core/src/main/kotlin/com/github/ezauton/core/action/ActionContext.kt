package com.github.ezauton.core.action

import com.github.ezauton.conversion.Duration
import com.github.ezauton.conversion.millis
import com.github.ezauton.core.utils.Clock

data class ActionContext(val clock: Clock) {

    suspend fun delay(millis: Long) {
        delay(millis.millis)
    }

    suspend fun delay(duration: Duration) {
        clock.delayFor(duration)
    }
}
