package com.team2502.ezauton.recorder;

import com.team2502.ezauton.trajectory.geometry.ImmutableVector;
import com.team2502.ezauton.utils.IClock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RecordFrame implements Serializable
{

    private List<ImmutableVector> pathPoints = new ArrayList<>();

    private Colour robotColour = new Colour(0,0,0,1);

    private long time;

    private RobotState robotState = new RobotState(0,0,0,1,2);

    public RecordFrame(List<ImmutableVector> pathPoints, Colour robotColour, RobotState robotState)
    {
        this.pathPoints = pathPoints;
        this.robotColour = robotColour;
        this.robotState = robotState;
        this.time = System.currentTimeMillis();
    }

    public RecordFrame(List<ImmutableVector> pathPoints, Colour robotColour, RobotState robotState, long time)
    {
        this(pathPoints,robotColour,robotState);
        this.time = time;
    }

    public RecordFrame(List<ImmutableVector> pathPoints, Colour robotColour, RobotState robotState, IClock clock)
    {
        this(pathPoints,robotColour,robotState);
        this.time = clock.getTime();
    }

    public List<ImmutableVector> getPathPoints()
    {
        return pathPoints;
    }

    public Colour getRobotColour()
    {
        return robotColour;
    }

    public long getTime()
    {
        return time;
    }

    public RobotState getRobotState()
    {
        return robotState;
    }
}
