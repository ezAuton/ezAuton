package com.github.ezauton.core.pathplanning

import com.github.ezauton.conversion.ScalarVector

import java.io.Serializable

/**
 * A section of a path (usually linear) which has similar laws (i.e. same transition between two speeds).
 */
interface PathSegment : Serializable {
    val absoluteDistanceEnd: Double

    val isBeginning: Boolean

    val isFinish: Boolean

    val from: ScalarVector

    val to: ScalarVector

    val length: Double

    val absoluteDistanceStart: Double

    fun getPoint(relativeDistance: Double): ScalarVector

    fun getClosestPoint(robotPos: ScalarVector): ScalarVector

    /**
     * @param linePos
     * @return The absolute distance on the path of a point on the line
     */
    fun getAbsoluteDistance(linePos: ScalarVector): Double

    fun getSpeed(absoluteDistance: Double): Double
}
