package com.github.ezauton.core.test.kotlin.util

import com.github.ezauton.core.action.TimedPeriodicAction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class ActionTest {

    @Test
    fun `overloaded periodic tries to become stable`() {
        var bool = false

        val timedPeriodicAction = TimedPeriodicAction(20, TimeUnit.MILLISECONDS, 2,
                TimeUnit.SECONDS, Runnable {
            if (!bool) {
                bool = true
                Thread.sleep(1_000)
            }
        })

        timedPeriodicAction.schedule().join(2_000)

        assertEquals(2_000/20.toDouble(),timedPeriodicAction.timesRun.toDouble(), 2.toDouble())
    }
}