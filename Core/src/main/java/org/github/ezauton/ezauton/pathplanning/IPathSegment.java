package org.github.ezauton.ezauton.pathplanning;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.github.ezauton.ezauton.trajectory.geometry.ImmutableVector;

import java.io.Serializable;

/**
 * A section of a path (usually linear) which has similar laws (i.e. same transition between two speeds).
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="@class")
public interface IPathSegment extends Serializable
{
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
