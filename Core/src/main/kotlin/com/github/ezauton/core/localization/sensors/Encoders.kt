package com.github.ezauton.core.localization.sensors

import com.github.ezauton.conversion.*
import com.github.ezauton.core.utils.Stopwatch

/**
 * A utility class used for encoder conversion
 */
object Encoders {
  fun fromTachometer(tachometer: Tachometer, stopwatch: Stopwatch): RotationalDistanceSensor {
    return object : RotationalDistanceSensor {

      override var position: Angle = zero()
        get() {
          field +=  stopwatch.pop() * tachometer.velocity
          return field
        }

      override val velocity: AngularVelocity get() = tachometer.velocity
    }
  }

  /**
   * Convert an Encoder into an TranslationalDistanceSensor
   *
   * @param distancePerUnit The distance traveled given the encoder rotated 1 unit.
   * For example, if our encoder had 4096 units in a rotation (as many do), and one
   * rotation was a 1.5 feet of travel distance (as it would be for a ~6 in wheel), then this value is 1.5/4096.
   * @param speedPerNativeSpeed Conversion factor from native units to distance per second
   * @param encoder Reference to encoder
   * @return An TranslationalDistanceSensor
   */
  fun toTranslationalDistanceSensor(
    distancePerUnit: Distance,
    speedPerNativeSpeed: LinearVelocity,
    encoder: RotationalDistanceSensor
  ): TranslationalDistanceSensor {
    return object : TranslationalDistanceSensor { // TODO: this is kinda jank
      override val position: Distance
        get() = encoder.position.value * distancePerUnit

      override val velocity: LinearVelocity
        get() = encoder.velocity.value * speedPerNativeSpeed
    }
  }
}
