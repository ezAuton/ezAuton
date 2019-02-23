package com.github.ezauton.wpilib.motors;


import com.github.ezauton.core.actuators.VelocityMotor;
import com.github.ezauton.core.actuators.VoltageMotor;
import com.github.ezauton.core.localization.sensors.Encoder;

/**
 * An interface representing your typical motor. It is able to be controller by either velocity or voltage, and has an encoder.
 */
public interface TypicalMotor extends VelocityMotor, Encoder, VoltageMotor {
}
