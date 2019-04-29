package com.github.ezauton.core.utils.math

import com.github.ezauton.core.trajectory.geometry.ImmutableVector

/**
 * Rotate the input vector theta radians counterclockwise
 *
 * @param vector The input vecto
 * @param theta  How much to rotate it by (radians)
 * @return The rotated vector
 */
fun ImmutableVector.rotate2D(theta: Double): ImmutableVector {
    val sin = esin(theta)
    val cos = ecos(theta)
    return ImmutableVector(get(0) * cos - get(1) * sin,
            get(0) * sin + get(1) * cos)
}

/**
 * Turn absolute coordinates into coordinates relative to the robot
 *
 * @param coordinateAbsolute The absolute coordinates
 * @param robotCoordAbs      The robot's absolute position
 * @param robotHeading       The robot's heading (radians)
 * @return `coordinateAbsolute` but relative to the robot
 */
fun absoluteToRelativeCoord(coordinateAbsolute: ImmutableVector, robotCoordAbs: ImmutableVector, robotHeading: Double): ImmutableVector {
    return coordinateAbsolute.sub(robotCoordAbs).rotate2D(-robotHeading)
}

fun relativeToAbsoluteCoord(coordinateRelative: ImmutableVector, robotCoordAbs: ImmutableVector, robotHeading: Double): ImmutableVector {
    return coordinateRelative.rotate2D(robotHeading) + robotCoordAbs
}
