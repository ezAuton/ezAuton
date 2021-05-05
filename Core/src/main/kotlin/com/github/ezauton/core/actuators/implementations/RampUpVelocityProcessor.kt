package com.github.ezauton.core.actuators.implementations

import com.github.ezauton.conversion.*
import com.github.ezauton.core.actuators.VelocityMotor
import com.github.ezauton.core.actuators.VelocityProcessor
import com.github.ezauton.core.localization.Updatable
import com.github.ezauton.core.utils.Stopwatch

/**
 * A velocity processor where the motor has a maximum accceleration
 */
class RampUpVelocityProcessor
/**
 * Create a RampUpVelocity processor
 *
 * @param velocityMotor The motor to apply the processed velocity to
 * @param clock The clock to keep time with
 * @param maxAccel The maximum acceleration of this motor.
 */
  (velocityMotor: VelocityMotor, private val maxAccel: AngularAcceleration) : VelocityProcessor(velocityMotor), Updatable {
  private val accelStopwatch: Stopwatch = Stopwatch.start()

  var lastVelocity = 0.radians / sec
    private set
  private var targetVelocity = 0.radians / sec

  /**
   * Update the motor to simulate acceleration over time
   *
   * @return True
   */
  override fun update(): Boolean {
    lastVelocity = if (targetVelocity > lastVelocity) {
      min(lastVelocity + maxAccel * accelStopwatch.pop(), targetVelocity) // TODO: make this better and use triangle integral + stopwatch
    } else {
      max(lastVelocity - maxAccel * accelStopwatch.pop(), targetVelocity)
    }
    toApply.runVelocity(lastVelocity)
    return true
  }

  /**
   * Make the motor accelerate up to a new velocity
   *
   * @param targetVelocity The new target velocity
   */
  override fun runVelocity(targetVelocity: AngularVelocity) {
    accelStopwatch.reset()
    this.targetVelocity = targetVelocity
  }
}
