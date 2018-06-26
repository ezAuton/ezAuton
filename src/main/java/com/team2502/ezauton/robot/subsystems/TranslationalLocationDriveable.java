package com.team2502.ezauton.robot.subsystems;

import com.team2502.ezauton.trajectory.geometry.ImmutableVector;

/**
 * Maps a speed of travel and a location to an action from the robot to go towards that location with the given speed.
 */
public interface TranslationalLocationDriveable
{
    /**
     * @param speed
     * @param loc
     * @return If the movement is possible
     */
    boolean driveTowardTransLoc(double speed, ImmutableVector loc);
}
