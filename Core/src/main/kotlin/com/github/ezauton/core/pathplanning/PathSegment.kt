package com.github.ezauton.core.pathplanning

import com.github.ezauton.conversion.ConcreteVector
import com.github.ezauton.conversion.SIUnit
import com.github.ezauton.conversion.ScalarVector

import java.io.Serializable

/**
 * A section of a path (usually linear) which has similar laws (i.e. same transition between two speeds).
 */
interface PathSegment<T: SIUnit<T>> : Serializable {
    val absoluteDistanceEnd: Double

    val isBeginning: Boolean

    val isFinish: Boolean

    val from: ConcreteVector<T>

    val to: ConcreteVector<T>

    val length: T

    val absoluteDistanceStart: Double

    fun getPoint(relativeDistance: Double): ConcreteVector<T>

    fun getClosestPoint(robotPos: ScalarVector): ConcreteVector<T>

    /**
     * @param linePos
     * @return The absolute distance on the path of a point on the line
     */
    fun getAbsoluteDistance(linePos: ConcreteVector<T>): Double

    fun getSpeed(absoluteDistance: T): Velocity
}
