package com.team2502.ezauton.pathplanning;

import com.team2502.ezauton.pathplanning.purepursuit.PPWaypoint;

public class PathGenerator
{

    private final PPWaypoint[] ppWaypoints;

    public PathGenerator(PPWaypoint... ppWaypoints)
    {
        this.ppWaypoints = ppWaypoints;
    }

    public Path generate()
    {
        for(int i = 0; i < ppWaypoints.length-1; i++)
        {
            PPWaypoint from = ppWaypoints[i];
            PPWaypoint to = ppWaypoints[i+1];
            if(i == 0)
            {

            }
            PathSegment pathSegment = new PathSegment(from,to,)
        }
    }
}
