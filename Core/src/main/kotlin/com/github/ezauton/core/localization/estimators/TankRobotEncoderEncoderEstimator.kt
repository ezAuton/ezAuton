package com.github.ezauton.core.localization.estimators

import com.github.ezauton.core.localization.RotationalLocationEstimator
import com.github.ezauton.core.localization.TankRobotVelocityEstimator
import com.github.ezauton.core.localization.TranslationalLocationEstimator
import com.github.ezauton.core.localization.Updateable
import com.github.ezauton.core.localization.sensors.TranslationalDistanceSensor
import com.github.ezauton.core.robot.TankRobotConstants
import com.github.ezauton.core.trajectory.geometry.ImmutableVector
import com.github.ezauton.core.utils.MathUtils

/**
 * Describes an object that can estimate the heading and absolute position of the robot solely using the encoders
 */
class TankRobotEncoderEncoderEstimator
/**
 * Create a TankRobotEncoderEstimator
 *
 * @param left      A reference to the encoder on the left side of the robot
 * @param right     A reference to the encoder on the right side of the robot
 * @param tankRobot A reference to an object containing data about the structure of the drivetrain
 */
(private val left: TranslationalDistanceSensor, private val right: TranslationalDistanceSensor, private val tankRobot: TankRobotConstants) : RotationalLocationEstimator, TranslationalLocationEstimator, TankRobotVelocityEstimator, Updateable {
    private var lastPosLeft: Double = 0.toDouble()
    private var lastPosRight: Double = 0.toDouble()
    private var init = false
    private var heading = 0.0
    private var location = ImmutableVector.origin(2)

    override val leftTranslationalWheelVelocity: Double
        get() = left.velocity

    override val rightTranslationalWheelVelocity: Double
        get() = right.velocity

    /**
     * Reset the heading and position of the location estimator
     */
    fun reset() //TODO: Suggestion -- Have an IPoseEstimator that implements Updateable, IRotationalEstimator, TranslationalLocationEstimator that also has a reset method
    {
        lastPosLeft = left.position
        lastPosRight = right.position
        location = ImmutableVector(0, 0)
        heading = 0.0
        init = true
    }

    override fun estimateHeading(): Double {
        return heading
    }

    override fun estimateLocation(): ImmutableVector {
        return location
    }

    /**
     * Update the calculation for the current heading and position. Call this as frequently as possible to ensure optimal results
     *
     * @return True
     */
    override fun update(): Boolean {
        if (!init) {
            throw IllegalArgumentException("Must be initialized! (call reset())")
        }

        val leftPosition = left.position
        val dl = leftPosition - lastPosLeft
        val rightPosition = right.position
        val dr = rightPosition - lastPosRight

        lastPosLeft = leftPosition
        lastPosRight = rightPosition

        val dLocation = MathUtils.Kinematics.getAbsoluteDPosCurve(dl, dr, tankRobot.lateralWheelDistance, heading)
        if (!dLocation.isFinite) {
            throw IllegalStateException("dLocation is $dLocation, which is not finite! dl = $dl, dr = $dr, heading = $heading")
        }
        location = location.add(dLocation)
        heading += MathUtils.Kinematics.getAngularDistance(dl, dr, tankRobot.lateralWheelDistance)
        return true
    }

    /**
     * @return The current velocity vector of the robot in 2D space.
     */
    override fun estimateAbsoluteVelocity(): ImmutableVector {
        return MathUtils.Geometry.getVector(avgTranslationalWheelVelocity, heading)
    }
}
