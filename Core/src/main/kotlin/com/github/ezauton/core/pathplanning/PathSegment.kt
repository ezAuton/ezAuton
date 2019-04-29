package com.github.ezauton.core.pathplanning

import com.github.ezauton.core.trajectory.geometry.ImmutableVector

import java.io.Serializable

/**
 * A section of a path (usually linear) which has similar laws (i.e. same transition between two speeds).
 */
interface PathSegment : Serializable {
    val absoluteDistanceEnd: Double

    val isBeginning: Boolean

    val isFinish: Boolean

    val from: ImmutableVector

    val to: ImmutableVector

    val length: Double

    val absoluteDistanceStart: Double

    fun getPoint(relativeDistance: Double): ImmutableVector

    fun getClosestPoint(robotPos: ImmutableVector): ImmutableVector

    /**
     * @param linePos
     * @return The absolute distance on the path of a point on the line
     */
    fun getAbsoluteDistance(linePos: ImmutableVector): Double

    fun getSpeed(absoluteDistance: Double): Double
}
