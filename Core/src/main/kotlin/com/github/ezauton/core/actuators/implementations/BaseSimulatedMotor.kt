package com.github.ezauton.core.actuators.implementations

import com.github.ezauton.conversion.Distance
import com.github.ezauton.conversion.LinearVelocity
import com.github.ezauton.conversion.Units
import com.github.ezauton.conversion.meters
import com.github.ezauton.core.actuators.VelocityMotor
import com.github.ezauton.core.localization.sensors.TranslationalDistanceSensor
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
  (clock: Clock) : VelocityMotor, TranslationalDistanceSensor {
  private val stopwatch: Stopwatch = Stopwatch(clock)

  /**
   * Assumed to be in dist/second
   */
  override var velocity: LinearVelocity = Units.mps(0.0)
    private set
  /**
   * @return The motor to which the velocity is being applied
   */


  /**
   * The motor to which the velocity will be applied
   *
   */
  var subscribed: VelocityMotor? = null
  override var position = 0.0.meters
    get(): Distance {
      stopwatch.resetIfNotInit()
      field += velocity * stopwatch.pop()
      return field
    }
    private set

  /**
   * @param targetVelocity The target speed for the motor to be ran at
   */
  override fun runVelocity(targetVelocity: LinearVelocity) {
    stopwatch.resetIfNotInit()
    if (subscribed != null) {
      subscribed!!.runVelocity(targetVelocity)
    }
    val popped = stopwatch.pop()
    position += velocity * popped
    this.velocity = targetVelocity
  }
}
