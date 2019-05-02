package com.github.ezauton.core.utils

import com.github.ezauton.core.action.DelayedAction
import com.github.ezauton.core.simulation.TimeWarpedSimulation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.ArrayList
import java.util.Arrays
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicLong

class TimeWarpedClockTest {
    @Test
    fun testFastClock() {
        val clock = TimeWarpedClock(10.0)

        val stopwatch = Stopwatch(clock)

        stopwatch.resetIfNotInit()

        try {
            Thread.sleep(5)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        assertEquals(50.0, stopwatch.pop(), 15.0)
    }

    @Test
    fun testFastClockScheduling() {

        val expectedNums = Arrays.asList<Number>(1000, 2000, 3000, 4000, 5000)

        val nums = ArrayList<Number>()
        val clock = TimeWarpedClock(10.0, -70) // -70 because my laptop usually takes 70ms

        // schedule tasks to run at 1000 ms, 2000 ms, 3000 ms, 4000 ms, 5000 ms
        for (i in 1..5) {
            clock.scheduleIn((i * 1000).toLong(), TimeUnit.MILLISECONDS) { nums.add(clock.time) }
        }

        try {
            Thread.sleep(550) // 5000 fake ms = 500 real ms, throw in 50 extra to be safe
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        assertEquals(expectedNums.size, nums.size)

        for (i in expectedNums.indices) {
            assertEquals(expectedNums[i].toDouble(), nums[i].toDouble(), 100.0)
        }
    }

    @Test
    @Throws(TimeoutException::class, ExecutionException::class)
    fun testFastClockWait() {
        val time = AtomicLong(0)

        val speed = 100000
        val sim = TimeWarpedSimulation(speed.toDouble())

        // 1000 fake seconds * 1 real sec / `1000 fake secs = 1 real sec
        sim.add(DelayedAction(speed, TimeUnit.SECONDS, { time.set(System.currentTimeMillis()) }))
        val init = System.currentTimeMillis()
        sim.runSimulation(10, TimeUnit.SECONDS)

        //        System.out.println("time.get - init = " + (time.get() - init));

        assertEquals(1000f, (time.get() - init).toFloat(), 100f)
    }
}
