package com.team2502.ezauton.recorder;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    public TankRobotState(double time, double x, double y, double heading, double robotWidth, double robotLength, double leftVel, double leftPos, double rightVel, double rightPos)
    {
        super(time, x, y, heading, robotWidth, robotLength);
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

    public TankRobotState() {}

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
