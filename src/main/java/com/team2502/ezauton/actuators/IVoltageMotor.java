package com.team2502.ezauton.actuators;

/**
 * A motor which can be run at a certain voltage
 */
public interface IVoltageMotor extends IMotor
{
    void runVoltage(double targetVoltage);
}
