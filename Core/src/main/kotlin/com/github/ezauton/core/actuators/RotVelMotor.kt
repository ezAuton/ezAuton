package com.github.ezauton.core.actuators

import com.github.ezauton.conversion.AngularVelocity

/**
 * A motor which can be run at a certain velocity
 */
interface RotVelMotor : Motor {
  /**
   * Run the motor at a certain velocity
   *
   * @param targetVelocity The speed to run the motor at
   */
  fun runVelocity(targetVelocity: AngularVelocity)
}
