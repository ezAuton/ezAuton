package com.github.ezauton.core.localization.estimators

import com.github.ezauton.core.localization.RotationalLocationEstimator
import com.github.ezauton.core.localization.TranslationalLocationEstimator
import com.github.ezauton.core.localization.Updatable
import com.github.ezauton.core.localization.sensors.TranslationalDistanceSensor
import com.github.ezauton.core.trajectory.geometry.ImmutableVector
import com.github.ezauton.core.utils.math.polarVector2D

/**
 * Describes an object that can track the location and heading of the robot using a rotational device
 * which can record angle (i.e. gyro) and a device which can record translational distance (i.e., encoder).
 */
class EncoderRotationEstimator
/**
 * Create an EncoderRotationEstimator
 *
 * @param rotationalLocationEstimator An object that can estimate our current heading
 * @param distanceSensor An encoder or encoder-like object.
 */
    (
    private val rotationalLocationEstimator: RotationalLocationEstimator,
    private val distanceSensor: TranslationalDistanceSensor
) : RotationalLocationEstimator, TranslationalLocationEstimator, Updatable {
    private var velocity: Double = 0.toDouble()
    private var lastPosition: Double = 0.toDouble()
    private var dPos: Double = 0.toDouble()
    private lateinit var dPosVec: ImmutableVector
    private lateinit var positionVec: ImmutableVector
    private var init = false

    /**
     * Set the current position to <0, 0>, in effect resetting the location estimator
     */
    fun reset() { // TODO: Reset heading
        lastPosition = distanceSensor.position
        dPosVec = ImmutableVector(0.0, 0.0)
        positionVec = ImmutableVector(0.0, 0.0)
        init = true
    }

    override fun estimateHeading(): Double {
        return rotationalLocationEstimator.estimateHeading()
    }

    /**
     * @return The current velocity vector of the robot in 2D space.
     */
    override fun estimateAbsoluteVelocity(): ImmutableVector {
        return polarVector2D(velocity, rotationalLocationEstimator.estimateHeading())
    }

    /**
     * @return The current location as estimated from the encoders
     */
    override fun estimateLocation() = positionVec

    /**
     * Update the calculation for the current heading and position. Call this as frequently as possible to ensure optimal results
     *
     * @return True
     */
    override fun update(): Boolean {
        if (!init) {
            throw IllegalArgumentException("Must be initialized! (call reset())")
        }
        if (rotationalLocationEstimator is Updatable) {
            (rotationalLocationEstimator as Updatable).update()
        }
        velocity = distanceSensor.velocity
        dPos = distanceSensor.position - lastPosition
        dPosVec = polarVector2D(dPos, rotationalLocationEstimator.estimateHeading())
        positionVec += dPosVec

        lastPosition = distanceSensor.position

        return true // TODO: Return false sometimes?
    }
}
