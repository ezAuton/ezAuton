package com.github.ezauton.core.actuators.implementations

import com.github.ezauton.core.actuators.VelocityMotor
import com.github.ezauton.core.localization.sensors.TranslationalDistanceSensor
import com.github.ezauton.core.utils.units.toSeconds
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
    override var velocity = 0.0
        private set
    /**
     * @return The motor to which the velocity is being applied
     */
    /**
     * Change the motor to which the velocity will be applied
     *
     * @param subscribed The new motor instance
     */
    var subscribed: VelocityMotor? = null
    override var position = 0.0
        get(): Double {
            stopwatch.resetIfNotInit()
            field += velocity * stopwatch.pop().toSeconds()
            return field
        }
        private set

    /**
     * @param targetVelocity The target speed for the motor to be ran at
     */
    override fun runVelocity(targetVelocity: Double) {
        stopwatch.resetIfNotInit()
        if (subscribed != null) {
            subscribed!!.runVelocity(targetVelocity)
        }
        val popped = stopwatch.pop().toSeconds()
        position += velocity * popped
        this.velocity = targetVelocity
    }
}
