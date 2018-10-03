package com.team2502.ezauton.recorder;

import java.io.Serializable;

/**
 * An immutable object for storing the state of the robot at a particular point in time.
 */
public class RobotState implements Serializable
{
    private double x;
    private double y;

    private double heading;

    private double robotWidth;
    private double robotLength;
    private double time;

    /**
     * An immutable
     * @param x The absolute x coordinate of the robot (in feet)
     * @param y The absolute y coordinate of the robot (in feet)
     * @param heading The heading of the robot in CCW radians (where 0 is facing ahead)
     * @param robotWidth The width of the robot in feet (the x-component of the robot)
     * @param robotLength The height of the robot in feet (the y-component of the robot)
     */
    public RobotState(double x, double y, double heading, double robotWidth, double robotLength, double time)
    {
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.robotWidth = robotWidth;
        this.robotLength = robotLength;
        this.time = time;
    }

    public RobotState(){}

    public double getTime()
    {
        return time;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getHeading()
    {
        return heading;
    }

    public double getRobotWidth()
    {
        return robotWidth;
    }

    public double getRobotLength()
    {
        return robotLength;
    }

    @Override
    public String toString()
    {
        return "RobotState{" +
               "x=" + x +
               ", y=" + y +
               ", heading=" + heading +
               ", robotWidth=" + robotWidth +
               ", robotLength=" + robotLength +
               '}';
    }
}
