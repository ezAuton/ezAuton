package com.github.ezauton.core.actuators

/**
 * A class which takes in a velocity, and automatically applies the velocity to another motor
 * This is nice for grouping multiple velocity processors on top of each other. Primarily used for simulated motors.
 */
abstract class VelocityProcessor(val toApply: RotVelMotor) : RotVelMotor
