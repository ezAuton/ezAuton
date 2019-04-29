package com.github.ezauton.core.simulator

import com.github.ezauton.core.action.Action
import com.github.ezauton.core.action.BaseAction
import com.github.ezauton.core.actuators.implementations.BaseSimulatedMotor
import com.github.ezauton.core.simulation.ModernSimulatedClock
import org.junit.jupiter.api.Test

import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

import org.junit.jupiter.api.Assertions.assertEquals

class SimulatedMotorTest {
    @Test
    @Throws(TimeoutException::class)
    fun testMotor() {

        val clock = ModernSimulatedClock()

        val action = BaseAction({
            val motor = BaseSimulatedMotor(clock)

            assertEquals(0.0, motor.position, 1E-6)
            motor.runVelocity(1.0)

            try {
                clock.sleep(1, TimeUnit.SECONDS)
            } catch (e: InterruptedException) {
                return
            }

            assertEquals(1.0, motor.position, 1E-6)

            try {
                clock.sleep(1, TimeUnit.SECONDS)
            } catch (e: InterruptedException) {
                return
            }

            assertEquals(2.0, motor.position, 1E-6)
            motor.runVelocity(10.0)
            assertEquals(2.0, motor.position, 1E-6)

            try {
                clock.sleep(1, TimeUnit.SECONDS)
            } catch (e: InterruptedException) {
                return
            }

            assertEquals(12.0, motor.position, 1E-6)
        })

        clock.add(action)
        clock.runSimulation(5, TimeUnit.SECONDS)

    }
}
