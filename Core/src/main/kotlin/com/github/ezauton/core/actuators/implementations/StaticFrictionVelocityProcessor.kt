package com.github.ezauton.core.actuators.implementations

import com.github.ezauton.conversion.AngularVelocity
import com.github.ezauton.core.actuators.RotVelMotor
import com.github.ezauton.core.actuators.VelocityProcessor
import com.github.ezauton.core.localization.sensors.Tachometer

/**
 * A velocity processor that does not run the motor if the target velocity is less than the minimum velocity that is required to move.
 */
class StaticFrictionVelocityProcessor
/**
 * Create a StaticFrictionVelocityProcessor
 *
 * @param velocitySensor An encoder
 * @param toApply The motor to apply the processed velocity to
 * @param minVelMove The minimum velocity to move the motor
 */
  (private val velocitySensor: Tachometer, toApply: RotVelMotor, private val minVelMove: AngularVelocity) : VelocityProcessor(toApply) {

  /**
   * Run the motor at the target velocity, unless the target velocity is too small and we are not moving
   *
   * @param targetVelocity The speed to run the motor at
   */
  override fun runVelocity(targetVelocity: AngularVelocity) {
    if (!velocitySensor.velocity.isApproxZero || targetVelocity.abs() >= minVelMove) {
      toApply.runVelocity(targetVelocity)
    }
  }
}
