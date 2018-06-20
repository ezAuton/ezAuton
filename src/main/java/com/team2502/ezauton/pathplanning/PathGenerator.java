package com.team2502.ezauton.pathplanning;

import com.team2502.ezauton.pathplanning.purepursuit.PPWaypoint;

import java.util.ArrayList;
import java.util.List;

public class PathGenerator
{

    private final PPWaypoint[] ppWaypoints;

    public PathGenerator(PPWaypoint... ppWaypoints)
    {
        this.ppWaypoints = ppWaypoints;
    }

    public Path generate()
    {
        List<PathSegment> pathSegments = new ArrayList<>();
        double distance = 0;
        for(int i = 0; i < ppWaypoints.length-1; i++)
        {
            PPWaypoint from = ppWaypoints[i];
            PPWaypoint to = ppWaypoints[i+1];
            double length = from.getLocation().dist(to.getLocation());

            //TODO: Update from RobotCode2018 style pathsegments
            PathSegment pathSegment = new PathSegment(from, to, i == 0, i == ppWaypoints.length - 2, distance, distance += length, length);
            pathSegments.add(pathSegment);
        }
    }
}
