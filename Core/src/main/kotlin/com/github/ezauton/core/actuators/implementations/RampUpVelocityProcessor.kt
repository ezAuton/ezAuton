package com.github.ezauton.core.actuators.implementations

import com.github.ezauton.core.actuators.VelocityMotor
import com.github.ezauton.core.actuators.VelocityProcessor
import com.github.ezauton.core.localization.Updatable
import com.github.ezauton.core.utils.Clock
import com.github.ezauton.core.utils.Stopwatch

import java.util.concurrent.TimeUnit

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
(velocityMotor: VelocityMotor, clock: Clock, private val maxAccel: Double) : VelocityProcessor(velocityMotor), Updatable {
    private val accelStopwatch: Stopwatch = Stopwatch(clock).reset()

    var lastVelocity = 0.0
        private set
    private var targetVelocity: Double = 0.toDouble()

    /**
     * Update the motor to simulate acceleration over time
     *
     * @return True
     */
    override fun update(): Boolean {
        lastVelocity = if (targetVelocity > lastVelocity) {
            Math.min(lastVelocity + maxAccel * accelStopwatch.pop(TimeUnit.SECONDS), targetVelocity) // TODO: make this better and use triangle integral + stopwatch
        } else {
            Math.max(lastVelocity - maxAccel * accelStopwatch.pop(TimeUnit.SECONDS), targetVelocity)
        }
        toApply.runVelocity(lastVelocity)
        return true
    }

    /**
     * Make the motor accelerate up to a new velocity
     *
     * @param targetVelocity The new target velocity
     */
    override fun runVelocity(targetVelocity: Double) {
        accelStopwatch.reset()
        this.targetVelocity = targetVelocity
    }
}