package com.github.ezauton.core.robot.implemented

import com.github.ezauton.conversion.*
import com.github.ezauton.core.actuators.VelocityMotor
import com.github.ezauton.core.localization.RotationalLocationEstimator
import com.github.ezauton.core.localization.TranslationalLocationEstimator
import com.github.ezauton.core.robot.TankRobotConstants
import com.github.ezauton.core.robot.subsystems.TranslationalLocationDrivable
import com.github.ezauton.core.utils.math.absoluteToRelativeCoord
import com.github.ezauton.core.utils.math.between
import com.github.ezauton.core.utils.math.calculateCurvature

/**
 * Describes the kinematics for a robot with a tank drivetrain
 */
class TankRobotTransLocDrivable
/**
 * Describes the kinematics to drive a tank-drive robot along an arc to get to a goal point
 *
 * @param leftMotor The motor on the left side
 * @param rightMotor The motor on the right side
 * @param translationalLocationEstimator An estimator for our absolute location
 * @param rotationalLocationEstimator An estimator for our heading
 * @param tankRobotConstants A data class containing constants regarding the structure of the tank drive robot, such as lateral wheel distance
 */
(private val leftMotor: VelocityMotor, private val rightMotor: VelocityMotor, private val translationalLocationEstimator: TranslationalLocationEstimator, private val rotationalLocationEstimator: RotationalLocationEstimator, private val tankRobotConstants: TankRobotConstants) : TranslationalLocationDrivable {
    var lastLeftTarget: LinearVelocity = 0.mps
        private set
    var lastRightTarget: LinearVelocity = 0.mps
        private set

    /**
     * Move the robot to a target location. Ideally, this would be run continuously.
     *
     * @param speed The maximum speed of the robot
     * @param loc The absolute coordinates of the target location
     * @return True
     */
    override fun driveTowardTransLoc(speed: LinearVelocity, loc: ConcreteVector<Distance>): Boolean {
        require(loc.dimension == 2) { "target location must be in R2" }
        require(loc.isFinite) { "target location $loc must contain real, finite numbers" }

        val wheelVelocities = getWheelVelocities(speed, loc)

        lastLeftTarget = wheelVelocities[0]

        leftMotor.runVelocity(lastLeftTarget)

        lastRightTarget = wheelVelocities[1]
        rightMotor.runVelocity(lastRightTarget)

        return true // always possible //TODO: Maybe sometimes return false?
    }

    /**
     * Drive the robot at a speed
     *
     * @param speed The target speed, where a positive value is forwards and a negative value is backwards
     * @return True, it is always possible to drive straight
     */
    override fun driveSpeed(speed: LinearVelocity): Boolean {
        leftMotor.runVelocity(speed)
        rightMotor.runVelocity(speed)
        return true
    }

    /**
     * Calculate how to get to a target location given a maximum speed
     *
     * @param speed The maximum speed (not velocity) the robot is allowed to go
     * @param loc THe absolute coordinates of the goal point
     * @return The wheel speeds in a vector where the 0th element is the left wheel speed and the 1st element is the right wheel speed
     */
    private fun getWheelVelocities(speed: LinearVelocity, loc: ConcreteVector<Distance>): ConcreteVector<LinearVelocity> {
        // TODO: lookover
        val relativeCoord = absoluteToRelativeCoord(loc, translationalLocationEstimator.estimateLocation(), rotationalLocationEstimator.estimateHeading())
        val curvature = calculateCurvature(relativeCoord)
        var bestVector: ConcreteVector<LinearVelocity>? = null

        val v_lMin = -speed
        val v_rMin = -speed

        val lateralWheelDistance = tankRobotConstants.lateralWheelDistance

        if (curvature.abs() < THRESHOLD_CURVATURE)
        // if we are a straight line ish (lines are not curvy -> low curvature)
        {
            return ConcreteVector.of(speed, speed)
        } else
        // if we need to go in a circle, we should calculate the wheel velocities so we hit our target radius AND our target tangential speed
        {

            // Formula for differential drive radius of cricle
            // r = L/2 * (vl + vr)/(vr - vl)
            // 2(vr - vl) * r = L(vl + vr)
            // L*vl + L*vr - 2r*vr + 2r*vl = 0
            // vl(L+2r) + vr(L-2r) = 0
            // vl(L+2r) = -vr(L-2r)
            // vl/vr = -(L+2r)/(L-2r)
            val r = 1 / curvature.value // ELIMINATING UNITS

            val velLeftToRightRatio = -(lateralWheelDistance + 2 * r) / (lateralWheelDistance - 2 * r)
            val velRightToLeftRatio = 1 / velLeftToRightRatio // invert the ratio

            // This first big repetitive section is just finding the largest possible velocities while maintaining a ratio.
            var score = LinearVelocity(Double.MIN_VALUE) // TODO: probably better way to do this

            var v_r = speed * velLeftToRightRatio

            if (v_r in v_rMin .. speed || v_r in speed .. v_rMin) {
                score = (speed + v_r).abs()
                bestVector = ConcreteVector.of(speed, v_r)
            }

            v_r = v_lMin * velLeftToRightRatio
            if (v_r in v_rMin .. speed || v_r in speed .. v_rMin) {
                val tempScore = (v_lMin + v_r).abs()
                if (tempScore > score) {
                    score = tempScore
                    bestVector = ConcreteVector.of(v_lMin, v_r)
                }
            }

            var v_l = speed * velRightToLeftRatio
            if (v_l in v_lMin .. speed || v_l in speed .. v_lMin) {
                val tempScore = (speed + v_l).abs()
                if (tempScore > score) {
                    score = tempScore
                    bestVector = ConcreteVector.of(v_l, speed)
                }
            }

            v_l = v_rMin * velRightToLeftRatio
            if (v_l in v_lMin .. speed || v_l in speed .. v_lMin) {
                val tempScore = (v_lMin + v_l).abs()
                if (tempScore > score) {
                    bestVector = ConcreteVector.of(v_l, v_rMin)
                }
            }

            if (bestVector == null) {
                val s = "bestVector is null! input: {speed: " + speed + ", targetLoc: " + loc + ", robotLoc: " + translationalLocationEstimator.estimateLocation() + "}"
                throw NullPointerException(s) // TODO: More informative error message
            }

            if ((bestVector.get(0) + bestVector.get(1)) / speed == -1.0) { // tangential vel and speed are opposite signs
                System.err.println("Robot is going the wrong direction!")
            }
        }

        return bestVector
    }

    companion object {

        /**
         * The minimum curvature, below which we are driving on a straight line
         */
        private val THRESHOLD_CURVATURE = 0.001.meters
    }
}
