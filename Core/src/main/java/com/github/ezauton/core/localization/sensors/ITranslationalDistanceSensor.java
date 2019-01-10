package com.github.ezauton.core.localization.sensors;

/**
 * Like an encoder but for translational distance instead of rotations. An example of a ITranslationalDistanceSensor is an {@link EncoderWheel}.
 */
public interface ITranslationalDistanceSensor
{
    double getPosition();

    double getVelocity();
}
