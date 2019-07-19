package com.github.ezauton.core.utils.math

import com.github.ezauton.conversion.*

/**
 * Rotate the input vector theta radians counterclockwise
 *
 * @param vector The input vecto
 * @param theta How much to rotate it by (radians)
 * @return The rotated vector
 */
fun <T> ConcreteVector<T>.rotate2D(theta: Angle): ConcreteVector<T>
        where T : SIUnit<T>, T : LinearUnit {
    val sin = esin(theta.value)
    val cos = ecos(theta.value)
    return ConcreteVector.of(get(0) * cos - get(1) * sin, get(0) * sin + get(1) * cos)
}

/**
 * Turn absolute coordinates into coordinates relative to the robot
 *
 * @param coordinateAbsolute The absolute coordinates
 * @param robotCoordAbs The robot's absolute position
 * @param robotHeading The robot's heading (radians)
 * @return `coordinateAbsolute` but relative to the robot
 */
fun <T> absoluteToRelativeCoord(coordinateAbsolute: ConcreteVector<T>, robotCoordAbs: ConcreteVector<T>, robotHeading: Angle): ConcreteVector<T>
        where T : SIUnit<T>, T : LinearUnit {
    return coordinateAbsolute.minus(robotCoordAbs).rotate2D(-robotHeading)
}

fun <T> relativeToAbsoluteCoord(coordinateRelative: ConcreteVector<T>, robotCoordAbs: ConcreteVector<T>, robotHeading: Angle): ConcreteVector<out T>
        where T : SIUnit<T>, T : LinearUnit {
    return coordinateRelative.rotate2D(robotHeading) + robotCoordAbs
}
