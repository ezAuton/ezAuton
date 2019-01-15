package com.github.ezauton.core.localization;

import com.github.ezauton.core.localization.sensors.IVelocityEstimator;
import com.github.ezauton.core.trajectory.geometry.ImmutableVector;

/**
 * Interface for any class that knows how fast the wheels on either side of the robot are going, given that the robot has a tank drivetrain
 */
public interface ITankRobotVelocityEstimator extends IVelocityEstimator
{
    /**
     * @return The absolute velocity of the robot
     */
    ImmutableVector estimateAbsoluteVelocity();

    /**
     * @return Velocity of the left wheel. Can be negative or positive.
     */
    double getLeftTranslationalWheelVelocity();

    /**
     * @return Velocity of the right wheel. Can be negative or positive.
     */
    double getRightTranslationalWheelVelocity();

    /**
     * @return Average velocity of both wheels. This will be the tangential velocity of the robot
     * if it is a normal tank robot.
     */
    default double getAvgTranslationalWheelVelocity()
    {
        return (getLeftTranslationalWheelVelocity() + getRightTranslationalWheelVelocity()) / 2D;
    }

    @Override
    default double getTranslationalVelocity(){
        return getAvgTranslationalWheelVelocity();
    }

    /**
     * @return The average wheel speed. NOTE: this will always be positive and can be non-zero even
     * if the robot has 0 translational velocity.
     */
    default double getAvgTranslationalWheelSpeed()
    {
        return (Math.abs(getLeftTranslationalWheelVelocity()) + Math.abs(getRightTranslationalWheelVelocity())) / 2F;
    }
}
