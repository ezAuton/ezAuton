package com.github.ezauton.core.actuators.implementations

import com.github.ezauton.conversion.LinearVelocity
import com.github.ezauton.core.actuators.VelocityMotor
import com.github.ezauton.core.actuators.VelocityProcessor
import com.github.ezauton.core.localization.sensors.TranslationalDistanceSensor

/**
 * A velocity processor that does not run the motor if the target velocity is less than the minimum velocity that is required to move.
 */
class StaticFrictionVelocityProcessor
/**
 * Create a StaticFrictionVelocityProcessor
 *
 * @param distanceSensor An encoder
 * @param toApply The motor to apply the processed velocity to
 * @param minVelMove The minimum velocity to move the motor
 */
(private val distanceSensor: TranslationalDistanceSensor, toApply: VelocityMotor, private val minVelMove: LinearVelocity) : VelocityProcessor(toApply) {

    /**
     * Run the motor at the target velocity, unless the target velocity is too small and we are not moving
     *
     * @param targetVelocity The speed to run the motor at
     */
    override fun runVelocity(targetVelocity: LinearVelocity) {
        if (!distanceSensor.velocity.isZero  || targetVelocity.abs() >= minVelMove) {
            toApply.runVelocity(targetVelocity)
        }
    }
}
