package com.github.ezauton.recorder.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ezauton.recorder.base.frame.RobotStateFrame;
import com.github.ezauton.core.trajectory.geometry.ImmutableVector;

public class TankRobotState extends RobotStateFrame
{
    @JsonProperty
    protected double leftVel;

    @JsonProperty
    protected double leftPos;

    @JsonProperty
    protected double rightVel;

    @JsonProperty
    protected double rightPos;

    public TankRobotState(double time, ImmutableVector pos, double heading, double robotWidth, double robotLength, double leftVel, double leftPos, double rightVel, double rightPos, ImmutableVector absoluteVelocity)
    {
        super(time, pos, heading, robotWidth, robotLength, absoluteVelocity);
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
