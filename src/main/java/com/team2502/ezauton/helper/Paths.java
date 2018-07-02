package com.team2502.ezauton.helper;

import com.team2502.ezauton.pathplanning.PP_PathGenerator;
import com.team2502.ezauton.pathplanning.Path;
import com.team2502.ezauton.pathplanning.purepursuit.PPWaypoint;

public class Paths
{
    public static Path STRAIGHT_12FT;

    static
    {
        PPWaypoint waypoint1 = PPWaypoint.simple2D(0, 0, 0, 3, -3);
        PPWaypoint waypoint2 = PPWaypoint.simple2D(0, 6, 5, 3, -3);
        PPWaypoint waypoint3 = PPWaypoint.simple2D(0, 12, 0, 3, -3);

        PP_PathGenerator pathGenerator = new PP_PathGenerator(waypoint1, waypoint2, waypoint3);
        STRAIGHT_12FT = pathGenerator.generate(0.05);
    }
}
