package com.github.ezauton.core.test.kotlin.util

import com.github.ezauton.core.action.simulation.ModernSimulatedClock
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test


class ClockTest {

    @Test
    fun `modern clock no run without start simulation`(){
        var ran = false

        val clock = ModernSimulatedClock()

        clock.scheduleAt(0){ ran = true }

        assertFalse(ran)
    }
}