package com.github.ezauton.core.actuators.implementations

import com.github.ezauton.conversion.LinearVelocity
import com.github.ezauton.conversion.Units
import com.github.ezauton.core.actuators.VelocityMotor
import com.github.ezauton.core.actuators.VelocityProcessor

/**
 * A velocity processor that makes the target motor respect a maximum speed
 */
class BoundedVelocityProcessor
/**
 * Create a BoundedVelocityProcessor
 *
 * @param toApply The motor to apply the processed velocity to
 * @param maxSpeed The maximum speed that the motor will be allowed to run at.
 */
  (toApply: VelocityMotor, private val maxSpeed: LinearVelocity) : VelocityProcessor(toApply) {

  init {
    if (maxSpeed <= Units.mps(0.0)) {
      throw IllegalArgumentException("maxSpeed must be a positive number!")
    }
  }

  /**
   * Run the motor at a target velocity, unless the velocity is larger than this motor's maximum velocity
   *
   * @param targetVelocity The speed to run the motor at
   */
  override fun runVelocity(targetVelocity: LinearVelocity) {
    when {
      targetVelocity > maxSpeed -> toApply.runVelocity(maxSpeed)
      targetVelocity < -maxSpeed -> toApply.runVelocity(-maxSpeed)
      else -> toApply.runVelocity(targetVelocity)
    }
  }
}
