package com.team2502.ezauton.actuators;

/**
 * Maps from a real input to what the output would actually be
 */
public interface IMotorBehavior
{
    double output(double input);
}
