package org.github.ezauton.ezauton.wpilib.motors;


import com.github.ezauton.core.actuators.IVelocityMotor;
import com.github.ezauton.core.actuators.IVoltageMotor;
import com.github.ezauton.core.localization.sensors.IEncoder;

public interface ITypicalMotor extends IVelocityMotor, IEncoder, IVoltageMotor
{
}
