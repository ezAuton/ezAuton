package com.github.ezauton.recorder.base.frame;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ezauton.core.trajectory.geometry.ImmutableVector;
import com.github.ezauton.recorder.SequentialDataFrame;

import java.io.Serializable;

/**
 * An immutable object for storing the state of the robot at a particular point in time.
 * <p>
 * Generic drivetrain format
 */
public class RobotStateFrame extends SequentialDataFrame implements Serializable {
    @JsonProperty
    private ImmutableVector pos;

    @JsonProperty
    private double heading;

    @JsonProperty
    private double robotWidth;

    @JsonProperty
    private double robotLength;

    @JsonProperty
    private ImmutableVector robotVelocity;

    /**
     * An immutable
     *
     * @param pos         The absolute coordinate position of the robot
     * @param heading     The heading of the robot in CCW radians (where 0 is facing ahead)
     * @param robotWidth  The width of the robot in feet (the x-component of the robot)
     * @param robotLength The height of the robot in feet (the y-component of the robot)
     */
    public RobotStateFrame(double time, ImmutableVector pos, double heading, double robotWidth, double robotLength, ImmutableVector robotVelocity) {
        super(time);
        this.pos = pos;
        this.heading = heading;
        this.robotWidth = robotWidth;
        this.robotLength = robotLength;
        this.robotVelocity = robotVelocity;
    }

    protected RobotStateFrame() {
    }

    public ImmutableVector getPos() {
        return pos;
    }

    public double getHeading() {
        return heading;
    }

    public double getRobotWidth() {
        return robotWidth;
    }

    public double getRobotLength() {
        return robotLength;
    }

    public ImmutableVector getRobotVelocity() {
        return robotVelocity;
    }

    @Override
    public String toString() {
        return "RobotState{" +
                "pos=" + pos +
                ", heading=" + heading +
                ", robotWidth=" + robotWidth +
                ", robotLength=" + robotLength +
                ", time=" + time +
                '}';
    }
}
