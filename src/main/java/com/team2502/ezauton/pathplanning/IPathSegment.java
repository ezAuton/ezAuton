package com.team2502.ezauton.pathplanning;

import com.team2502.ezauton.trajectory.geometry.ImmutableVector;

/**
 * A section of a path (usually linear) which has similar laws (i.e. same transition between two speeds).
 */
public interface IPathSegment
{
    boolean isBeginning();

    boolean isFinish();

    ImmutableVector getFrom();

    ImmutableVector getTo();

    double getLength();

    ImmutableVector getClosestPoint(ImmutableVector robotPos);

    /**
     * @param linePos
     * @return The absolute distance on the path of a point on the line
     */
    double getAbsoluteDistance(ImmutableVector linePos);

    double getSpeed(double absoluteDistance);
}
