package com.github.ezauton.core.pathplanning.ramsete

import com.github.ezauton.core.pathplanning.Path
import com.github.ezauton.core.robot.TankRobotConstants
import com.github.ezauton.core.utils.MathUtils

//TODO: move to src/main/kotlin? not sure about good kotlin practices when interop'ing with java -- rm
class RamseteMovementStrategy(val b: Double, val zeta: Double, val stopTolerance: Double, val tankRobotConstants: TankRobotConstants, path: Path, dt: Double) {

    val ramsetePath: TimeStateSeries = TimeStateSeries(path, dt)
    var lastOutput: Output? = null;

    fun recalculate(time: Double, robotPose: Pose): Output {
        val (desiredPose, desiredOutput) = ramsetePath.getDesiredPose(time)
        val (v_d, w_d) = desiredOutput
        var (x_e: Double, y_e: Double, theta_e: Double) = desiredPose - robotPose

        var k: Double = 2 * zeta * Math.sqrt(w_d * w_d + b * v_d * v_d)

        var v_output: Double = v_d * Math.cos(theta_e) + k * (Math.cos(x_e) + Math.sin(y_e))
        var w_output: Double = w_d + b * v_d * sinc(theta_e) * (Math.cos(x_e) - Math.sin(y_e)) + k * theta_e

        //TODO: Velocity bounding?
        return calculateInverseKinematics(ControlOutput(v_output, w_output))
    }

    private fun calculateInverseKinematics(controlOutput: ControlOutput): Output {
        val (v: Double, w: Double) = controlOutput;

        val v_left: Double = v - (tankRobotConstants.lateralWheelDistance * w) / 2
        val v_right: Double = v + (tankRobotConstants.lateralWheelDistance * w) / 2

        var output = Output(v_left, v_right)
        lastOutput = output
        return output
    }

    /**
     * @return tracking error (x, y, theta)
     */
    fun error(currentPose: Pose, desiredPose: Pose): Pose {
        return desiredPose - currentPose;
        // I think it's supposed to be in absolute coordinates?
//        val dPose = desiredPose - currentPose
//        val theta = currentPose.theta
//
//        val rotate2DCW = ImmutableVector(dPose.x, dPose.y).rotate2DCW(theta)
//
//        return Pose(rotate2DCW[0], rotate2DCW[1], dPose.theta)
    }

    private fun sinc(double: Double): Double {
        if (MathUtils.epsilonEquals(double, 0.0)) {
            // limit as sin(x)/x approaches 0
            return 1.0
        } else {
            return MathUtils.sin(double) / double
        }
    }

    fun isFinished(currentPose: Pose): Boolean {
//        val (pose, _) = ramsetePath.finalState
//        return currentPose.dist(pose) < stopTolerance
        return if (lastOutput != null) lastOutput!!.leftVelocity < 1e-5 && lastOutput!!.rightVelocity < 1e-5 else false;
    }

    data class Pose(val x: Double, val y: Double, val theta: Double) {
        operator fun minus(other: Pose): Pose {
            return Pose(x - other.x, y - other.y, theta - other.theta)
        }

        fun dist(other: Pose): Double {
            val error = this - other
            return Math.sqrt(error.x * error.x + error.y * error.y + error.theta * error.theta)
        }
    }

    data class ControlOutput(val velocity: Double, val turningRate: Double)

    data class Output(val leftVelocity: Double, val rightVelocity: Double)

    data class DesiredState(val pose: Pose, val desiredOutput: ControlOutput)
}
