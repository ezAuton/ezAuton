package com.github.ezauton.core.pathplanning;

import com.github.ezauton.core.trajectory.geometry.ImmutableVector;

import java.io.Serializable;

/**
 * A section of a path (usually linear) which has similar laws (i.e. same transition between two speeds).
 */
public interface PathSegment extends Serializable {
    double getAbsoluteDistanceEnd();

    boolean isBeginning();

    boolean isFinish();

    ImmutableVector getFrom();

    ImmutableVector getTo();

    double getLength();

    ImmutableVector getPoint(double relativeDistance);

    double getAbsoluteDistanceStart();

    ImmutableVector getClosestPoint(ImmutableVector robotPos);

    /**
     * @param linePos
     * @return The absolute distance on the path of a point on the line
     */
    double getAbsoluteDistance(ImmutableVector linePos);

    double getSpeed(double absoluteDistance);
}
