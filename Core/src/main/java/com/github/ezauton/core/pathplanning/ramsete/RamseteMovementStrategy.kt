package com.github.ezauton.core.pathplanning.ramsete

import com.github.ezauton.core.trajectory.geometry.ImmutableVector
import com.github.ezauton.core.utils.MathUtils

class RamseteMovementStrategy() {

    fun calculateFor(currentPose: Pose, desiredPose: Pose, desiredVelocity: Double, gain1: Double, gain2: Double, gain3: Double)
    {
        val output = outputOf(currentPose, desiredPose, desiredVelocity, gain1, gain2, gain3)
        val (e1, e2, e3) = error(currentPose, desiredPose) // TODO: should cache
//        return Output(desiredVelocity * MathUtils.cos(e3)- output.turningRate, desiredPose.theta)
    }


    fun outputOf(currentPose: Pose, desiredPose: Pose, desiredVelocity: Double, gain1: Double, gain2: Double, gain3: Double): ControlOutput {
        val (e1, e2, e3) = error(currentPose, desiredPose)
        return ControlOutput(-gain1 * e1, -gain2 * desiredVelocity * sinc(e3) * e2 - gain3 * e3)
    }

    /**
     * @return tracking error (x, y, theta)
     */
    fun error(currentPose: Pose, desiredPose: Pose): Pose {
        val dPose = desiredPose - currentPose
        val theta = currentPose.theta

        val rotate2DCW = ImmutableVector(dPose.x, dPose.y).rotate2DCW(theta)

        return Pose(rotate2DCW[0], rotate2DCW[1], dPose.theta)
    }

    private fun sinc(double: Double): Double {
        return MathUtils.sin(double) / double
    }

    data class Pose(val x: Double, val y: Double, val theta: Double) {
        operator fun minus(other: Pose): Pose {
            return Pose(x - other.x, y - other.y, theta - other.theta)
        }
    }

    data class ControlOutput(val velocity: Double, val turningRate: Double)

    data class Output(val velocity: Double, val turningRate: Double)
}
