package com.team2502.ezauton.actuators;

/**
 * A motor which can be run at a certain velocity
 */
public interface IVelocityMotor extends IMotor
{
    void runVelocity(double velocity);
}
