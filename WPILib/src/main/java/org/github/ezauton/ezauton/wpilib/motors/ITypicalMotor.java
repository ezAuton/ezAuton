package org.github.ezauton.ezauton.wpilib.motors;

import org.github.ezauton.ezauton.actuators.IVelocityMotor;
import org.github.ezauton.ezauton.actuators.IVoltageMotor;
import org.github.ezauton.ezauton.localization.sensors.IEncoder;

public interface ITypicalMotor extends IVelocityMotor, IEncoder, IVoltageMotor
{
}
