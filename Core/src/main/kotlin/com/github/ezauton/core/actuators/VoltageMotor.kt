package com.github.ezauton.core.actuators

/**
 * A motor which can be run at a certain voltage
 */
interface VoltageMotor : Motor {
  fun runVoltage(targetVoltage: Double)
}
