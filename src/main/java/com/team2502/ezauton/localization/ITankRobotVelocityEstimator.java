package com.team2502.ezauton.localization;

import com.team2502.ezauton.trajectory.geometry.ImmutableVector;

/**
 * Interface for any class that will estimate position details of our robot
 */
public interface ITankRobotVelocityEstimator
{
    /**
     * @return The absolute velocity of the robot
     */
    ImmutableVector estimateAbsoluteVelocity();

    double getLeftWheelVelocity();

    double getRightWheelVelocity();

    default double avgWheelVelocity()
    {
        return (getLeftWheelVelocity() + getRightWheelVelocity()) / 2D;
    }

    default double avgWheelSpeed()
    {
        return (Math.abs(getLeftWheelVelocity()) + Math.abs(getRightWheelVelocity())) / 2F;
    }
}
