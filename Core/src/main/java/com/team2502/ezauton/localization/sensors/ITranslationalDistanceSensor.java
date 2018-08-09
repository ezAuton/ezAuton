package com.team2502.ezauton.localization.sensors;

/**
 * Like an encoder but for translational distance instead of rotations. An example of a ITranslationalDistanceSensor is an {@link EncoderWheel}.
 */
//TODO: Suggestion -- Let encoder be able to measure positiion and velocity
public interface ITranslationalDistanceSensor
{
    double getPosition();

    double getVelocity();
}
