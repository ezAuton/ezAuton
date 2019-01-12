package com.github.ezauton.core.test.kotlin.util

import com.github.ezauton.core.action.BaseAction
import com.github.ezauton.core.action.simulation.ModernSimulatedClock
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.IllegalStateException


class ClockTest {

    private fun newSim() = ModernSimulatedClock()

    @Test
    fun `modern simulated clock schedule test`(){
        val clock = newSim()

        Assertions.assertEquals(0, clock.time) // Should be 0 by default
        var switch = 0

        clock.scheduleAt(1){ if(switch == 2) switch++ }
        clock.scheduleNow { if(switch == 0) switch++ }
        clock.scheduleAt(0){ if(switch == 1) switch++ }
        clock.scheduleAt(1){ if(switch == 3) switch++ }

        clock.runSimulation(100, TimeUnit.MILLISECONDS)

        Assertions.assertEquals(4, switch)
    }

    @Test
    fun `test modern clock sleep no sim`(){
        assertThrows<IllegalStateException> {
            val sim = newSim()
            sim.sleep(2,TimeUnit.SECONDS)
        }
    }

    @Test
    fun `test modern clock sleep same thread`(){
        assertThrows<IllegalStateException> {
            val sim = newSim()
            val action = BaseAction{ Thread.sleep(150 )} // note this is A HORRIBLE PRACTICE, just for unit testing...
            sim.add(action)
            sim.runSimulation(1_000, TimeUnit.MILLISECONDS)
            sim.sleep(2,TimeUnit.MILLISECONDS)
        }
    }

    @Test
    fun `test modern clock timeout`() {

        assertThrows<IllegalArgumentException> { newSim().runSimulation(0, TimeUnit.MILLISECONDS)}
        assertThrows<IllegalArgumentException> { newSim().runSimulation(-1, TimeUnit.MILLISECONDS)}


        val action = BaseAction{ Thread.sleep(3 )} // note this is A HORRIBLE PRACTICE, just for unit testing...

        assertThrows<TimeoutException> { newSim().add(action).runSimulation(1, TimeUnit.MILLISECONDS) }
    }

    @Test
    fun `modern clock no run without start simulation`(){
        var ran = false

        val clock = ModernSimulatedClock()

        clock.scheduleAt(0){ ran = true }
        clock.scheduleAt(1){ ran = true }

        assertFalse(ran)
    }
}