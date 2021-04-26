package com.github.ezauton.core.actuators.implementations

import com.github.ezauton.conversion.Angle
import com.github.ezauton.conversion.AngularVelocity
import com.github.ezauton.conversion.radians
import com.github.ezauton.conversion.zero
import com.github.ezauton.core.actuators.VelocityMotor
import com.github.ezauton.core.localization.sensors.RotationalDistanceSensor
import com.github.ezauton.core.utils.Clock
import com.github.ezauton.core.utils.Stopwatch

/**
 * Describes a simulated motor with an encoder. The motor has infinite acceleration
 */
class BaseSimulatedMotor
/**
 * Create a basic simulated motor
 *
 * @param clock The clock to keep track of time with
 */
  (clock: Clock) : VelocityMotor, RotationalDistanceSensor {
  private val stopwatch: Stopwatch = Stopwatch(clock)

  /**
   * Assumed to be in dist/second
   */
  override var velocity: AngularVelocity = zero()
    private set
  /**
   * @return The motor to which the velocity is being applied
   */


  /**
   * The motor to which the velocity will be applied
   *
   */
  var subscribed: VelocityMotor? = null

  override var position = 0.0.radians
    get(): Angle {
      stopwatch.resetIfNotInit()
      field += velocity * stopwatch.pop()
      return field
    }
    private set

  /**
   * @param targetVelocity The target speed for the motor to be ran at
   */
  override fun runVelocity(targetVelocity: AngularVelocity) {
    stopwatch.resetIfNotInit()
    if (subscribed != null) {
      subscribed!!.runVelocity(targetVelocity)
    }
    val popped = stopwatch.pop()
    position += velocity * popped
    this.velocity = targetVelocity
  }
}
