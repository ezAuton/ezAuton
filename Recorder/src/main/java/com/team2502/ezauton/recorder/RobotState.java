package com.team2502.ezauton.recorder;

import java.io.Serializable;

/**
 * An immutable object for storing the state of the robot at a particular point in time.
 */
public class RobotState implements Serializable
{
    private final int x;
    private final int y;

    private final float heading;

    private final int robotWidth;
    private final int robotHeight;

    /**
     * An immutable
     * @param x The absolute x coordinate of the robot (in feet)
     * @param y The absolute y coordinate of the robot (in feet)
     * @param heading The heading of the robot in CCW radians (where 0 is facing ahead)
     * @param robotWidth The width of the robot in feet (the x-component of the robot)
     * @param robotLength The height of the robot in feet (the y-component of the robot)
     */
    public RobotState(int x, int y, float heading, int robotWidth, int robotLength)
    {
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.robotWidth = robotWidth;
        this.robotHeight = robotLength;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public float getHeading()
    {
        return heading;
    }

    public int getRobotWidth()
    {
        return robotWidth;
    }

    public int getRobotHeight()
    {
        return robotHeight;
    }
}
