package com.github.ezauton.core.actuators

import com.github.ezauton.conversion.LinearVelocity

interface TransVelMotor: Motor {
  fun runVelocity(linearVelocity: LinearVelocity)
}
