package com.team2502.ezauton.localization;

import org.joml.ImmutableVector2f;

/**
 * Interface for any class that will estimate position details of our robot
 */
public interface ITranslationalVelocityEstimator
{
    /**
     * @return The absolute velocity of the robot
     */
    ImmutableVector2f estimateAbsoluteVelocity();

    float getLeftWheelSpeed();

    float getRightWheelSpeed();

    float estimateSpeed();

    default float avgWheelSpeed()
    {
        return (Math.abs(getLeftWheelSpeed()) + Math.abs(getRightWheelSpeed())) / 2F;
    }
}
