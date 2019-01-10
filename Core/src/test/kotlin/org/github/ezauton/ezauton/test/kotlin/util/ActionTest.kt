package org.github.ezauton.ezauton.test.kotlin.util

import org.github.ezauton.ezauton.action.TimedPeriodicAction
import org.github.ezauton.ezauton.localization.Updateable
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.TimeUnit

class ActionTest {

    @Test
    fun `overloaded periodic tries to become stable`() {
        var bool = false

        val timedPeriodicAction = TimedPeriodicAction(20, TimeUnit.MILLISECONDS, 2,
                TimeUnit.SECONDS, Updateable {
            if (!bool) {
                bool = true
                Thread.sleep(1_000)
            }
            return@Updateable true
        })

        timedPeriodicAction.schedule().join(2_000)

        Assert.assertEquals(2_000/20.toDouble(),timedPeriodicAction.timesRun.toDouble(), 2.toDouble())
    }
}