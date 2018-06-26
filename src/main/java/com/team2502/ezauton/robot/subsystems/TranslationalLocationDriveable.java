package com.team2502.ezauton.robot.subsystems;

import com.team2502.ezauton.trajectory.geometry.ImmutableVector;

public interface TranslationalLocationDriveable
{
    /**
     *
     * @param speed
     * @param loc
     * @return If the movement is possible
     */
    boolean driveTowardTransLoc(double speed, ImmutableVector loc);
}
