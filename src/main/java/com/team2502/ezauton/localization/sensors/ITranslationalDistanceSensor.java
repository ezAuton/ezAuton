package com.team2502.ezauton.localization.sensors;

/**
 * Like an encoder but for translational distance
 */
public interface ITranslationalDistanceSensor
{
    double getPosition();

    double getVelocity();
}
