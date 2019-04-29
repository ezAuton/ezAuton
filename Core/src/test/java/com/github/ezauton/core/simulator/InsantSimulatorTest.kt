package com.github.ezauton.core.simulator

import com.github.ezauton.core.action.Action
import com.github.ezauton.core.action.TimedPeriodicAction
import com.github.ezauton.core.simulation.ModernSimulatedClock
import org.junit.jupiter.api.Test

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicLong

import org.junit.jupiter.api.Assertions.assertEquals

class InsantSimulatorTest {
    @Test
    @Throws(TimeoutException::class)
    fun testABC() {

        val sum = AtomicLong()

        val actionA = TimedPeriodicAction(20, TimeUnit.SECONDS)
                .addRunnable({ a -> { sum.addAndGet(a.stopwatch.read()) } })

        val actionB = TimedPeriodicAction(20, TimeUnit.SECONDS)
                .addRunnable({ a ->
                    {
                        val l = sum.addAndGet(-a.stopwatch.read(TimeUnit.MILLISECONDS))
                        assertEquals(0, l)
                    }
                })

        val clock = ModernSimulatedClock()

        clock
                .add(actionA)
                .add(actionB)
                .runSimulation(1000, TimeUnit.SECONDS)

        assertEquals(0, sum.get())
    }
}
