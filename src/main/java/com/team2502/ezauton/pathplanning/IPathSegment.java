package com.team2502.ezauton.pathplanning;

import com.team2502.ezauton.trajectory.geometry.ImmutableVector;

public interface IPathSegment
{
    boolean isBeginning();
    boolean isFinish();
    ImmutableVector getFrom();
    ImmutableVector getTo();
    double getLength();
    ImmutableVector getClosestPoint(ImmutableVector robotPos);

    /**
     *
     * @param linePos
     * @return The absolute distance on the path of a point on the line
     */
    double getAbsoluteDistance(ImmutableVector linePos);

    double getSpeed(double absoluteDistance);
}
