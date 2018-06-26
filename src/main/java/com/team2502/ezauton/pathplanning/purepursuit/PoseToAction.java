package com.team2502.ezauton.pathplanning.purepursuit;

import com.team2502.ezauton.trajectory.geometry.ImmutableVector;

public interface PoseToAction
{
    /**
     * @param pose The pose of the robot (normally returned by Pure Pursuit)
     * @return The action that the robot should take
     */
    ImmutableVector poseToAction(ImmutableVector pose, double wheelSpeed);
}
