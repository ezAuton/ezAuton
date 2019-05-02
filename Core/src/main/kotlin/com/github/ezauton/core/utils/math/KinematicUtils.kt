package com.github.ezauton.core.utils.math

import com.github.ezauton.core.trajectory.geometry.ImmutableVector

/**
 * Get the 1D position of the robot given p0, v0, a0, and dt. Uses elementary physics formulas.
 *
 * @param posInit
 * @param velocityInit
 * @param accelerationInit
 * @param dt
 * @return
 */
fun getPos(posInit: Double, velocityInit: Double, accelerationInit: Double, dt: Double): Double {
    return posInit + velocityInit * dt + (1 / 2f).toDouble() * accelerationInit * dt * dt
}

/**
 * @param leftVel
 * @param rightVel
 * @param lateralWheelDistance
 * @return positive CCW, negative CW
 */
fun getAngularDistance(leftVel: Double, rightVel: Double, lateralWheelDistance: Double): Double {
    return (rightVel - leftVel) / lateralWheelDistance
}

/**
 * @param vL
 * @param vR
 * @param l
 * @return The radius of the circle traveling across .. positive if CCW
 */
fun getTrajectoryRadius(vL: Double, vR: Double, l: Double): Double {
    return l * (vR + vL) / (2 * (vR - vL))
}

/**
 * The relative difference in position using arcs
 *
 * @param distanceLeft
 * @param distanceRight
 * @param lateralWheelDistance
 * @return
 */
fun getRelativeDPosCurve(distanceLeft: Double, distanceRight: Double, lateralWheelDistance: Double): ImmutableVector {
    // To account for an infinite pathplanning radius when going straight
    if (Math.abs(distanceLeft - distanceRight) <= Math.abs(distanceLeft + distanceRight) * 1E-2) {
        // Probably average is not needed, but it may be useful over long distances
        return ImmutableVector(0.0, (distanceLeft + distanceRight) / 2.0)
    }
    val w = getAngularDistance(distanceLeft, distanceRight, lateralWheelDistance)

    val r = getTrajectoryRadius(distanceLeft, distanceRight, lateralWheelDistance)

    val dxRelative = -r * (1 - ecos(-w))
    val dyRelative = -r * esin(-w)

    return ImmutableVector(dxRelative, dyRelative)
}

fun getTangentialSpeed(wheelL: Double, wheelR: Double): Double {
    return (wheelL + wheelR) / 2.0
}

fun getAbsoluteDPosLine(vL: Double, vR: Double, dt: Double, robotHeading: Double): ImmutableVector {
    val tangentialSpeed = getTangentialSpeed(vL, vR)
    val tangentialDPos = getTangentialSpeed(vL, vR) * dt
    val dPos = VECTOR_FORWARD.mul(tangentialDPos)
    return dPos.rotate2D(robotHeading)
}

fun getAbsoluteDPosCurve(vL: Double, vR: Double, l: Double, robotHeading: Double): ImmutableVector {
    return getRelativeDPosCurve(vL, vR, l).rotate2D(robotHeading)
}

/**
 * Turn NavX angle into radians (navX 0 degrees is facing (0,1) ... increases CW)
 *
 * @param yawDegTot What the NavX is reading
 * @return The angle in radians, between 0 and 2pi.
 */

fun navXToRad(yawDegTot: Double): Double {
    var yawDeg = -yawDegTot % 360
    if (yawDeg < 0) {
        yawDeg += 360
    }
    return deg2Rad(yawDeg)
}

/**
 * turn an angle without bounds (-inf,inf) to [0,360)
 *
 * @param angle Whatever the navX is reading
 * @return An angle between 0 and 360, in degrees
 */
fun navXBound(angle: Double): Double {
    val bounded = angle % 360
    return if (bounded < 0) {
        360 + bounded
    } else bounded
}
