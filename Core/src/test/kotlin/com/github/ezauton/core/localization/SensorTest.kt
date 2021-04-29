package com.github.ezauton.core.localization

import com.github.ezauton.conversion.*
import com.github.ezauton.core.localization.sensors.EncoderWheel
import com.github.ezauton.core.localization.sensors.Encoders
import com.github.ezauton.core.localization.sensors.Tachometer
import com.github.ezauton.core.utils.ManualClock
import com.github.ezauton.core.utils.Stopwatch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private val tachometer get() = Tachometer.withVelocity(100.radians / sec)

@ExperimentalCoroutinesApi
class SensorTest {

  @Test
  fun `tachometer to encoder`() {
    val clock = ManualClock()
    val stopwatch = Stopwatch(clock).reset()

    val encoder = Encoders.fromTachometer(tachometer, stopwatch)

    assertEquals(0.0.radians, encoder.position)

    clock.time += 1.seconds

    assertEquals(100.0.radians, encoder.position)

    clock.time += 3.seconds

    assertEquals(400.0.radians, encoder.position)
  }

  @Test
  fun `encoder wheel`() {
    val clock = ManualClock()
    val stopwatch = Stopwatch(clock).reset()


    val encoder = Encoders.fromTachometer(tachometer, stopwatch)
    val encoderWheel = EncoderWheel(encoder, 3.0.meters / Math.PI)

    assertEquals(0.0.meters, encoderWheel.position)

    clock.time += 1.seconds

    assertEquals(100.0 * 3.0.meters, encoderWheel.position)

    clock.time += 3.seconds

    assertEquals(400.0 * 3.0.meters, encoderWheel.position)

    assertEquals(100.0 * 3.0.meters / sec, encoderWheel.velocity)
  }

  @Test
  fun `encoder wheel multiplier`() {
    val clock = ManualClock()
    val stopwatch = Stopwatch(clock).reset()

    val encoder = Encoders.fromTachometer(tachometer, stopwatch)
    val encoderWheel = EncoderWheel(encoder, 3.0.meters / Math.PI)

    assertEquals(100.0 * 3.0.meters / sec, encoderWheel.velocity)

    assertEquals(0.0.meters, encoderWheel.position)

    clock.time += 1.seconds

    assertEquals(100.0 * 3.0.meters, encoderWheel.position)

    encoderWheel.multiplier = 2.0

    clock.time += 3.seconds

    assertEquals(100.0 * 3.0 + 2.0 * 300.0 * 3.0, encoderWheel.position.value, 1E-6)

    assertEquals(100.0 * 3.0 * 2.0.meters / sec, encoderWheel.velocity)
  }
}
