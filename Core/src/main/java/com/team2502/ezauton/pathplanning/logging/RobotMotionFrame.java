package com.team2502.ezauton.pathplanning.logging;

import com.team2502.ezauton.trajectory.geometry.ImmutableVector;

import java.io.Serializable;

/**
 * Data class that describes the position of a robot at a particular time. Useful in conjunction with visualization tools to figure out what's wrong
 */
public class RobotMotionFrame implements Serializable
{


    protected final double robotX;
    protected final double robotY;
    protected final double tangentialVelocity;
    protected final double heading;

    /**
     * Create a RobotMotionFrame
     *
     * @param robotPos           The position of the robot
     * @param tangentialVelocity The tangential velocity of the robot
     * @param heading            The heading of the robot
     */
    public RobotMotionFrame(ImmutableVector robotPos, double tangentialVelocity, double heading)
    {

        this.robotX = robotPos.get(0);
        this.robotY = robotPos.get(1);

        this.tangentialVelocity = tangentialVelocity;
        this.heading = heading;
    }

    public double getRobotX()
    {
        return robotX;
    }

    public double getRobotY()
    {
        return robotY;
    }

}