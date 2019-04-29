package com.github.ezauton.core.robot.subsystems

import com.github.ezauton.core.trajectory.geometry.ImmutableVector

/**
 * Maps a speed of travel and a location to an action from the robot to go towards that location with the given speed.
 */
interface TranslationalLocationDrivable {
    /**
     * Move the robot from our current location to a target location while respecting a maximum speed
     *
     * @param speed The maximum speed of the robot
     * @param loc   The target location of the robot
     * @return If the movement is possible
     */
    fun driveTowardTransLoc(speed: Double, loc: ImmutableVector): Boolean

    /**
     * Drive in any direction (normally straight) at a certain speed. Good for low speeds
     *
     * @param speed How fast to go
     * @return If the movement is possible
     */
    fun driveSpeed(speed: Double): Boolean
}
