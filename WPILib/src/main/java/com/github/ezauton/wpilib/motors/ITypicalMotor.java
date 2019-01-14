package com.github.ezauton.wpilib.motors;


import com.github.ezauton.core.actuators.IVelocityMotor;
import com.github.ezauton.core.actuators.IVoltageMotor;
import com.github.ezauton.core.localization.sensors.IEncoder;

/**
 * An interface representing your typical motor. It is able to be controller by either velocity or voltage, and has an encoder.
 */
public interface ITypicalMotor extends IVelocityMotor, IEncoder, IVoltageMotor
{
}
