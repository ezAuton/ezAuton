package com.github.ezauton.core.localization

import com.github.ezauton.core.actuators.VelocityMotor
import com.github.ezauton.core.localization.sensors.DrivenEncoderWheel
import com.github.ezauton.core.localization.sensors.EncoderWheel
import com.github.ezauton.core.localization.sensors.Encoders
import com.github.ezauton.core.utils.ManualClock
import com.github.ezauton.core.utils.Stopwatch
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class SensorTest {

    @Test
    fun `tachometer to encoder`() {
        val clock = ManualClock()
        val stopwatch = Stopwatch(clock).reset()

        val encoder = Encoders.fromTachometer({ 100.0 }, stopwatch)

        assertEquals(0.0, encoder.position)

        clock.addTime(1, TimeUnit.SECONDS)

        assertEquals(100.0, encoder.position)

        clock.addTime(3, TimeUnit.SECONDS)
        assertEquals(400.0, encoder.position)
    }

    @Test
    fun `encoder wheel`() {
        val clock = ManualClock()
        val stopwatch = Stopwatch(clock).reset()

        val encoder = Encoders.fromTachometer({ 100.0 }, stopwatch)
        val encoderWheel = EncoderWheel(encoder, 3.0 / Math.PI, 1.0)

        assertEquals(0.0, encoderWheel.position)

        clock.addTime(1, TimeUnit.SECONDS)

        assertEquals(100.0 * 3.0, encoderWheel.position)

        clock.addTime(3, TimeUnit.SECONDS)
        assertEquals(400.0 * 3.0, encoderWheel.position)

        assertEquals(100.0 * 3.0, encoderWheel.velocity)
    }

    @Test
    fun `encoder wheel multiplier`() {
        val clock = ManualClock()
        val stopwatch = Stopwatch(clock).reset()

        val encoder = Encoders.fromTachometer({ 100.0 }, stopwatch)
        val encoderWheel = EncoderWheel(encoder, 3.0 / Math.PI, 1.0)

        assertEquals(100.0 * 3.0, encoderWheel.velocity)

        assertEquals(0.0, encoderWheel.position)

        clock.addTime(1, TimeUnit.SECONDS)

        assertEquals(100.0 * 3.0, encoderWheel.position)

        encoderWheel.distanceMultiplier = 2.0

        clock.addTime(3, TimeUnit.SECONDS)
        assertEquals(100.0 * 3.0 + 2.0 * 300.0 * 3.0, encoderWheel.position, 1E-6)

        assertEquals(100.0 * 3.0 * 2.0, encoderWheel.velocity)
    }

    @Test
    fun `driven encoder wheel test`() {
        val clock = ManualClock()
        val stopwatch = Stopwatch(clock).reset()


        val velocityMotor = object : VelocityMotor {
            var velocity: Double = 0.0 // Hello! I am in rev/s!

            override fun runVelocity(targetVelocity: Double) {
                velocity = targetVelocity
            }
        }

        val encoder = Encoders.fromTachometer({ velocityMotor.velocity }, stopwatch);

        val drivenEncoderWheel = DrivenEncoderWheel(encoder, 3.0 / Math.PI, 1.0, velocityMotor)

        assertEquals(0.0, drivenEncoderWheel.velocity)
        assertEquals(0.0, drivenEncoderWheel.position)

        clock.addTime(1, TimeUnit.SECONDS) // we should not be moving
        assertEquals(0.0, drivenEncoderWheel.velocity)
        assertEquals(0.0, drivenEncoderWheel.position)

        drivenEncoderWheel.setLinearVelocity(10.0) // Let's go 10ft/s

        clock.addTime(1, TimeUnit.SECONDS);
        assertEquals(10.0 / 3.0, velocityMotor.velocity)
        assertEquals(10.0, drivenEncoderWheel.position)
        assertEquals(10.0, drivenEncoderWheel.velocity)


    }
}
