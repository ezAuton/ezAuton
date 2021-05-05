package com.github.ezauton.core.localization.estimators

import com.github.ezauton.conversion.*
import com.github.ezauton.core.localization.RotLocEst
import com.github.ezauton.core.localization.TransLocEst
import com.github.ezauton.core.localization.Updatable
import com.github.ezauton.core.localization.sensors.TranslationalDistanceSensor
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
  private val rotationalLocationEstimator: RotLocEst,
  private val distanceSensor: TranslationalDistanceSensor
) : RotLocEst, TransLocEst, Updatable {
  private var velocity: LinearVelocity = zero()
  private var lastPosition: Distance  = zero()
  private var dPos: Distance = zero()
  private lateinit var dPosVec: ConcreteVector<Distance>
  private lateinit var positionVec: ConcreteVector<Distance>
  private var init = false

  /**
   * Set the current position to <0, 0>, in effect resetting the location estimator
   */
  fun reset() { // TODO: Reset heading
    lastPosition = distanceSensor.position
    dPosVec = vec(0.0, 0.0)
    positionVec = vec(0.0, 0.0)
    init = true
  }

  override fun estimateHeading(): Angle {
    return rotationalLocationEstimator.estimateHeading()
  }

  /**
   * @return The current velocity vector of the robot in 2D space.
   */
  override fun estimateAbsoluteVelocity(): ConcreteVector<LinearVelocity> {
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
