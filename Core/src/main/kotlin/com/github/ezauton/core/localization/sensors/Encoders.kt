package com.github.ezauton.core.localization.sensors

import com.github.ezauton.conversion.second
import com.github.ezauton.core.utils.Stopwatch

/**
 * A utility class used for encoder conversion
 */
object Encoders {
  fun fromTachometer(tachometer: Tachometer, stopwatch: Stopwatch): RotationalDistanceSensor {
    return object : RotationalDistanceSensor {

      override var position = 0.0
        get() {
          field += stopwatch.pop().convert(second) * tachometer.velocity
          return field
        }

      override val velocity: Double
        get() = tachometer.velocity
    }
  }

  /**
   * Convert an Encoder into an TranslationalDistanceSensor
   *
   * @param feetPerUnit The distance traveled given the encoder rotated 1 unit.
   * For example, if our encoder had 4096 units in a rotation (as many do), and one
   * rotation was a 1.5 feet of travel distance (as it would be for a ~6 in wheel), then this value is 1.5/4096.
   * @param fpsPerNativeSpeed Conversion factor from native units to distance per second
   * @param enc Reference to encoder
   * @return An TranslationalDistanceSensor
   */
  fun toTranslationalDistanceSensor(
    feetPerUnit: Double,
    fpsPerNativeSpeed: Double,
    enc: RotationalDistanceSensor
  ): TranslationalDistanceSensor {
    return object : TranslationalDistanceSensor {
      override val position: Double
        get() = enc.position * feetPerUnit

      override val velocity: Double
        get() = enc.velocity * fpsPerNativeSpeed
    }
  }
}
