package com.team2502.ezauton.localization;

import org.joml.ImmutableVector;

/**
 * Interface for any class that will estimate position details of our robot
 */
public interface ITranslationalVelocityEstimator
{
    /**
     * @return The absolute velocity of the robot
     */
    ImmutableVector estimateAbsoluteVelocity();

    double getLeftWheelSpeed();

    double getRightWheelSpeed();

    double estimateSpeed();

    default double avgWheelSpeed()
    {
        return (Math.abs(getLeftWheelSpeed()) + Math.abs(getRightWheelSpeed())) / 2F;
    }
}
