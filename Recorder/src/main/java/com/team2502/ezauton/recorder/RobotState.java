package com.team2502.ezauton.recorder;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.ezauton.ezauton.trajectory.geometry.ImmutableVector;

import java.io.Serializable;

/**
 * An immutable object for storing the state of the robot at a particular point in time.
 * <p>
 * Generic drivetrain format
 */
public class RobotState extends SequentialDataFrame implements Serializable
{
    @JsonProperty
    protected ImmutableVector pos;

    @JsonProperty
    protected double heading;

    @JsonProperty
    protected double robotWidth;

    @JsonProperty
    protected double robotLength;

    /**
     * An immutable
     *
     * @param pos         The absolute coordinate position of the robot
     * @param heading     The heading of the robot in CCW radians (where 0 is facing ahead)
     * @param robotWidth  The width of the robot in feet (the x-component of the robot)
     * @param robotLength The height of the robot in feet (the y-component of the robot)
     */
    public RobotState(double time, ImmutableVector pos, double heading, double robotWidth, double robotLength)
    {
        super(time);
        this.pos = pos;
        this.heading = heading;
        this.robotWidth = robotWidth;
        this.robotLength = robotLength;
    }

    protected RobotState() {}

    public double getTime()
    {
        return time;
    }

    public ImmutableVector getPos()
    {
        return pos;
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
               "pos=" + pos +
               ", heading=" + heading +
               ", robotWidth=" + robotWidth +
               ", robotLength=" + robotLength +
               ", time=" + time +
               '}';
    }
}
