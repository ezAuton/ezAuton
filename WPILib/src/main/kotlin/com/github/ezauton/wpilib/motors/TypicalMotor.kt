package com.github.ezauton.wpilib.motors

import com.github.ezauton.core.actuators.RotVelMotor
import com.github.ezauton.core.actuators.VoltageMotor
import com.github.ezauton.core.localization.sensors.RotationalDistanceSensor

/**
 * An interface representing your typical motor. It is able to be controller by either velocity or voltage, and has an encoder.
 */
interface TypicalMotor : RotVelMotor, RotationalDistanceSensor, VoltageMotor
