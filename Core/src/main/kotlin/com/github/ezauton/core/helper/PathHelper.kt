package com.github.ezauton.core.helper

import com.github.ezauton.core.pathplanning.PP_PathGenerator
import com.github.ezauton.core.pathplanning.Path
import com.github.ezauton.core.pathplanning.purepursuit.PPWaypoint

/**
 * Class containing sample paths for Pure Pursuit
 * <br></br>
 * This class only contains static members.
 * <br></br>
 * Useful for testing ezAuton on a new robot.
 */
object PathHelper {
    /**
     * Describes a path 12 (ft? m?) long reaching a max velocity of 5 (ft/s? m/s?) with a max accel and decel of 3 (ft/s^2? m/s^2)
     */
    var STRAIGHT_12UNITS: Path

    init {
        val waypoint1 = PPWaypoint.simple2D(0.0, 0.0, 0.0, 3.0, -3.0)
        val waypoint2 = PPWaypoint.simple2D(0.0, 6.0, 5.0, 3.0, -3.0)
        val waypoint3 = PPWaypoint.simple2D(0.0, 12.0, 0.0, 3.0, -3.0)

        val pathGenerator = PP_PathGenerator(waypoint1, waypoint2, waypoint3)
        STRAIGHT_12UNITS = pathGenerator.generate(0.05)
    }
}
