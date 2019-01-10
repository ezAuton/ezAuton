package com.github.ezauton.core.actuators;

/**
 * A motor which can be run at a certain voltage
 */
public interface IVoltageMotor extends IMotor
{
    void runVoltage(double targetVoltage);
}
