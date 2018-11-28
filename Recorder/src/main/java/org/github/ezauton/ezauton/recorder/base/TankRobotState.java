package org.github.ezauton.ezauton.recorder.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.ezauton.ezauton.trajectory.geometry.ImmutableVector;

public class TankRobotState extends RobotState
{
    @JsonProperty
    protected double leftVel;

    @JsonProperty
    protected double leftPos;

    @JsonProperty
    protected double rightVel;

    @JsonProperty
    protected double rightPos;

    public TankRobotState(double time, ImmutableVector pos, double heading, double robotWidth, double robotLength, double leftVel, double leftPos, double rightVel, double rightPos)
    {
        super(time, pos, heading, robotWidth, robotLength);
        this.leftVel = leftVel;
        this.leftPos = leftPos;
        this.rightVel = rightVel;
        this.rightPos = rightPos;
    }

    public TankRobotState(double leftVel, double leftPos, double rightVel, double rightPos)
    {
        this.leftVel = leftVel;
        this.leftPos = leftPos;
        this.rightVel = rightVel;
        this.rightPos = rightPos;
    }

    private TankRobotState() {}

    public double getLeftVel()
    {
        return leftVel;
    }

    public double getLeftPos()
    {
        return leftPos;
    }

    public double getRightVel()
    {
        return rightVel;
    }

    public double getRightPos()
    {
        return rightPos;
    }
}
