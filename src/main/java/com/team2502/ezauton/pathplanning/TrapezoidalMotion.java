package com.team2502.ezauton.pathplanning;

import com.team2502.ezauton.pathplanning.purepursuit.PoseToWheelSpeed;
import com.team2502.ezauton.trajectory.geometry.ImmutableVector;

public class TrapezoidalMotion implements PoseToWheelSpeed
{
    @Override
    public double poseToWheelSpeed(Path path, ImmutableVector currentPose)
    {
        PathSegment current = path.getCurrent();
        return current;
    }
}
