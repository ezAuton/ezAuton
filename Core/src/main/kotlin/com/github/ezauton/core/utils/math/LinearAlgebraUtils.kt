package com.github.ezauton.core.utils.math

import com.github.ezauton.conversion.ConcreteVector
import com.github.ezauton.conversion.SIUnit
import com.github.ezauton.conversion.ScalarVector

/**
 * Rotate the input vector theta radians counterclockwise
 *
 * @param vector The input vecto
 * @param theta How much to rotate it by (radians)
 * @return The rotated vector
 */
fun ScalarVector.rotate2D(theta: Double): ScalarVector {
    val sin = esin(theta)
    val cos = ecos(theta)
    return ScalarVector(get(0) * cos - get(1) * sin,
            get(0) * sin + get(1) * cos)
}

/**
 * Turn absolute coordinates into coordinates relative to the robot
 *
 * @param coordinateAbsolute The absolute coordinates
 * @param robotCoordAbs The robot's absolute position
 * @param robotHeading The robot's heading (radians)
 * @return `coordinateAbsolute` but relative to the robot
 */
fun absoluteToRelativeCoord(coordinateAbsolute: ScalarVector, robotCoordAbs: ScalarVector, robotHeading: Double): ScalarVector {
    return coordinateAbsolute.minus(robotCoordAbs).rotate2D(-robotHeading)
}

fun <T : SIUnit<T>> absoluteToRelativeCoord(coordinateAbsolute: ConcreteVector<T>, robotCoordAbs: ConcreteVector<T>, robotHeading: Double): ScalarVector {
    return coordinateAbsolute.minus(robotCoordAbs).rotate2D(-robotHeading)
}

fun relativeToAbsoluteCoord(coordinateRelative: ScalarVector, robotCoordAbs: ScalarVector, robotHeading: Double): ScalarVector {
    return coordinateRelative.rotate2D(robotHeading) + robotCoordAbs
}
