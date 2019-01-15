package com.github.ezauton.core.test.kotlin.util

import com.github.ezauton.core.action.ActionGroup
import com.github.ezauton.core.action.BaseAction
import com.github.ezauton.core.action.DelayedAction
import com.github.ezauton.core.action.TimedPeriodicAction
import com.github.ezauton.core.kotlin.wrapType
import com.github.ezauton.core.simulation.ModernSimulatedClock
import com.github.ezauton.core.utils.TimeWarpedClock
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

        assertEquals(2_000 / 20.toDouble(), timedPeriodicAction.timesRun.toDouble(), 2.toDouble())
    }

    @Test
    fun `action group of parallels test`() {

        var counter = 0


        val actionGroup = ActionGroup.ofParallels(
                BaseAction { counter++ },
                BaseAction { counter++ },
                BaseAction { counter++ }
        )

        ModernSimulatedClock()
                .add(actionGroup)
                .runSimulation(1, TimeUnit.SECONDS)
        assertEquals(3, counter)
    }

    @Test
    fun `action group of wrappers test`() {

        var counter = 0

        val actionGroup = ActionGroup(

                DelayedAction (30, TimeUnit.SECONDS)
                        .onFinish { if (counter == 0) counter++ }
                        .wrapType(ActionGroup.Type.WITH),

                DelayedAction (20, TimeUnit.SECONDS)
                        .onFinish { if (counter == 1) counter++ }
                        .wrapType(ActionGroup.Type.PARALLEL),

                DelayedAction(10, TimeUnit.SECONDS)
                        .wrapType(ActionGroup.Type.SEQUENTIAL)
        )

        val clock = TimeWarpedClock(10.0, 0)

        actionGroup.schedule(clock).join()

        assertEquals(2, counter)


    }
}
