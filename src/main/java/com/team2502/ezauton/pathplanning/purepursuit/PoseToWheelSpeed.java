package com.team2502.ezauton.pathplanning.purepursuit;

import com.team2502.ezauton.pathplanning.Path;
import com.team2502.ezauton.trajectory.geometry.ImmutableVector;

public interface PoseToWheelSpeed
{
    double poseToWheelSpeed(Path path, ImmutableVector currentPose);
}
