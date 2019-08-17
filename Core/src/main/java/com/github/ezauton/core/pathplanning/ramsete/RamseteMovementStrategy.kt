package com.github.ezauton.core.pathplanning.ramsete

import com.github.ezauton.core.pathplanning.Path
import com.github.ezauton.core.robot.TankRobotConstants
import com.github.ezauton.core.trajectory.geometry.ImmutableMatrix
import com.github.ezauton.core.trajectory.geometry.ImmutableVector
import com.github.ezauton.core.utils.MathUtils
import java.lang.RuntimeException
import java.lang.StringBuilder
import kotlin.math.max

//TODO: move to src/main/kotlin? not sure about good kotlin practices when interop'ing with java -- rm
class RamseteMovementStrategy(val b: Double, val zeta: Double, val stopTolerance: Double, val tankRobotConstants: TankRobotConstants, path: Path, dt: Double) {

    val ramsetePath: TimeStateSeries = TimeStateSeries(path, dt)
    var lastOutput: Output? = null
    var lastTime = 0.0

    var ramseteFrame: Map<String, Any> = mapOf(
            "v_d" to 0.0, //
            "w_d" to 0.0, //
            "x_e" to 0.0, //
            "y_e" to 0.0, //
            "theta_e" to 0.0, //
            "robot_theta" to 0.0, //
            "k" to 0.0, //
            "ramv_first" to 0.0,
            "ramv_second" to 0.0,
            "ramv" to 0.0, //
            "ramw_second" to 0.0,
            "ramw_third" to 0.0,
            "ramw" to 0.0, //
            "robot_pose" to Pose.identity(),
            "desired_pose" to Pose.identity(),
            "pose_error" to Pose.identity()
    )

//    var ramsetePoseInfo: Map<String, Pose> = mapOf(
//
//    )

    fun recalculate(time: Double, robotPose: Pose, max_left: Double, max_right: Double): Output {
        lastTime = time;
        val (desiredPose, desiredOutput) = ramsetePath.getDesiredPose(time)
        val (v_d, w_d) = desiredOutput
        var (x_e: Double, y_e: Double, theta_e) = error(robotPose, desiredPose)

        theta_e = MathUtils.Geometry.simplifyAngleCentered0(theta_e)// Account for circular nature of angles

        if(theta_e > Math.PI || theta_e < -Math.PI) {
            throw RuntimeException()
        }

        var k = 2 * zeta * Math.sqrt(w_d * w_d + b * v_d * v_d)

        var robotTheta = robotPose.theta // radians
        if (robotTheta < -Math.PI) robotTheta += 2 * Math.PI
        else if (robotTheta > Math.PI) robotTheta -= 2 * Math.PI

        //FIXME works only on left turns. for right turns to work, x_e and y_e  must be swapped. I think this is an angle issue? help!!! --rm
        var ramv_first = v_d * Math.cos(theta_e)
        var ramv_second = k * y_e //(x_e * Math.cos(robotTheta) + y_e * Math.sin(robotTheta))
        var ramv: Double = ramv_first + ramv_second

        var ramw_second = b * v_d * sinc(theta_e) * -x_e //(y_e * Math.cos(robotTheta) - x_e * Math.sin(robotTheta))
        var ramw_third = k * theta_e
        var ramw: Double = w_d + ramw_second + ramw_third


        //TODO: Velocity bounding?
        var invKinematics = calculateInverseKinematics(ControlOutput(ramv, ramw), max_left, max_right)
//        println("v, w = ${ramv}, ${ramw}, l+r=${invKinematics.leftVelocity},${invKinematics.rightVelocity}")

        println("ramv = ${ramv}")
        if (ramv < 0) {
            println("\n\n\n")
            println("WARN: ramv < 0")
            println("k = ${k}")
            println("robotTheta = ${robotTheta}")
            println("x_e = ${x_e}")
            println("y_e = ${y_e}")
            println("second = $ramv_second")
            println("\n\n\n")
        }
        if (Math.abs(ramw) < Math.abs(w_d)) {
            println("\n\n\n")
            println("WARN: w_d adjusted to be less aggressive")
            println("w_d = ${w_d}")
            println("first = $ramw_second")
            println("second = $ramw_third")
            println("theta_e = ${theta_e}")
            println("\n\n\n")
        }

        ramseteFrame = mapOf(
                "v_d" to v_d, //
                "w_d" to w_d, //
                "x_e" to x_e, //
                "y_e" to y_e, //
                "theta_e" to theta_e, //
                "robot_theta" to robotTheta, //
                "k" to k, //
                "ramv_first" to ramv_first,
                "ramv_second" to ramv_second,
                "ramv" to ramv, //
                "ramw_second" to ramw_second,
                "ramw_third" to ramw_third,
                "ramw" to ramw,
                "robot_pose" to robotPose.toString(),
                "desired_pose" to desiredPose.toString(),
                "pose_error" to error(robotPose, desiredPose).toString()

        )



        return invKinematics
    }

    private fun calculateInverseKinematics(controlOutput: ControlOutput, max_left: Double, max_right: Double): Output {
        val (v: Double, w: Double) = controlOutput;

        var v_left: Double = v - (tankRobotConstants.lateralWheelDistance * w) / 2
        var v_right: Double = v + (tankRobotConstants.lateralWheelDistance * w) / 2

//        val min_right = -max_right
//        val min_left = -max_left
//
//        var scaling_ratio_fromright = Double.MIN_VALUE
//        if (v_right > max_right) {
//            scaling_ratio_fromright = max_right / v_right;
//        } else if (v_right < min_right) {
//            scaling_ratio_fromright = min_right / v_right;
//        }
//
//        var scaling_ratio_fromleft = Double.MIN_VALUE
//        if (v_left > max_left) {
//            scaling_ratio_fromleft = max_left / v_left;
//        } else if (v_left < min_left) {
//            scaling_ratio_fromleft = min_left / v_left;
//        }
//
//        var scaling_ratio = max(scaling_ratio_fromleft, scaling_ratio_fromright)
//
//        if (scaling_ratio > Double.MIN_VALUE) {
//            v_left *= scaling_ratio
//            v_right *= scaling_ratio
//        }

        var output = Output(v_left, v_right)
        lastOutput = output
        return output
    }

    fun error(currentPose: Pose, desiredPose: Pose): Pose {
        return desiredPose - currentPose
        // val currentTheta = currentPose.theta
        // val rotate2DCW  = dPoseAbs.run { ImmutableVector(x,y) }
        //
        // return Pose.from(rotate2DCW[0], rotate2DCW[1], dPoseAbs.theta)
    }

    private fun sinc(double: Double): Double {
        return if (MathUtils.epsilonEquals(double, 0.0)) {
            // maclaurin series approximation for sinx/x
            1.0 - 1.0 / 6.0 * double * double
        } else {
            MathUtils.sin(double) / double
        }
    }

    fun isFinished(currentPose: Pose): Boolean {
//        val (pose, _) = ramsetePath.finalState
//        return currentPose.dist(pose) < stopTolerance
//        return if (lastOutput != null) lastOutput!!.leftVelocity < 1e-5 && lastOutput!!.rightVelocity < 1e-5 else false;
        return false
    }

    data class Pose private constructor(val x: Double, val y: Double, val theta: Double) {

        companion object {
            fun from(x: Double, y: Double, theta: Double): Pose {
                return Pose(x, y, MathUtils.Geometry.simplifyAngleCentered0(theta))
            }

            fun from(tVec: ImmutableVector, theta: Double): Pose {
                return from(tVec.get(0), tVec.get(1), theta);
            }

            fun identity(): Pose {
                return from(0.0, 0.0, 0.0)
            }
        }

        operator fun minus(other: Pose): Pose {
            // reqires this - other + other = this
            return other.inverse().compose(this)
        }

        fun inverse(): Pose {
            val rotatedTranslationVec = MathUtils.LinearAlgebra.rotate2D(ImmutableVector(x, y), -theta).mul(-1.0)
            return from(rotatedTranslationVec, -theta)
        }

        fun compose(other: Pose): Pose {
            val newTheta = this.theta + other.theta
            val newTVec = MathUtils.LinearAlgebra.rotate2D(ImmutableVector(other.x, other.y), theta).add(ImmutableVector(this.x, this.y))

            return from(newTVec, newTheta)
        }

        fun dist(other: Pose): Double {
            val error = this - other
            return Math.sqrt(error.x * error.x + error.y * error.y + error.theta * error.theta)
        }

        fun equals(other: Pose): Boolean {
            val epsilon = 1e-3
            return MathUtils.epsilonEquals(this.x, other.x, epsilon) && MathUtils.epsilonEquals(this.y, other.y, epsilon) && MathUtils.epsilonEquals(this.theta, other.theta, epsilon)
        }

        override fun toString(): String {
            return "Pose(" +
                    "x=${String.format("%.04f", x)}," +
                    "y=${String.format("%.04f", y)}," +
                    "theta=${String.format("%.04f", theta)}" +
                    ")"
        }


    }

    data class ControlOutput(val velocity: Double, val turningRate: Double)

    data class Output(val leftVelocity: Double, val rightVelocity: Double)

    data class DesiredState(val pose: Pose, val desiredOutput: ControlOutput)
}
