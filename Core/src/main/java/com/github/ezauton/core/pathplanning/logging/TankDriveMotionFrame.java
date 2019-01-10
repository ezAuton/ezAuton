package com.github.ezauton.core.pathplanning.logging;

import com.github.ezauton.core.trajectory.geometry.ImmutableVector;

import java.io.Serializable;

/**
 * Data class that describes the position and velocity of a tank-drive robot at a particular time. Useful in conjunction with visualization tools to figure out what's wrong
 */
public class TankDriveMotionFrame extends RobotMotionFrame implements Serializable
{
    protected final double leftVel;
    protected final double rightVel;

    protected final double leftPos;
    protected final double rightPos;

    /**
     * Create a TankDriveMotionFrame
     *
     * @param robotPos     Position of the robot
     * @param leftVel      Velocity of the left wheel(s)
     * @param rightVel     Velocity of the right wheel(s)
     * @param leftPos      Position of the left encoder
     * @param rightPos     Position of the right encoder
     * @param radius       Radius of the robot motion circle
     * @param circleCenter Center of the robot motion circle
     */
    public TankDriveMotionFrame(ImmutableVector robotPos, double leftVel, double rightVel, double heading, double leftPos, double rightPos, double radius, ImmutableVector circleCenter)
    {
        super(robotPos, (leftVel + rightVel) / 2, heading);

        this.leftVel = leftVel;
        this.rightVel = rightVel;
        this.leftPos = leftPos;
        this.rightPos = rightPos;

    }

    public double getLeftVel()
    {
        return leftVel;
    }

    public double getRightVel()
    {
        return rightVel;
    }

    public double getLeftPos()
    {
        return leftPos;
    }

    public double getRightPos()
    {
        return rightPos;
    }
}