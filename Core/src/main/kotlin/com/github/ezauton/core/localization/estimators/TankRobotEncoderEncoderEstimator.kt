package com.github.ezauton.core.localization.estimators

import com.github.ezauton.conversion.*
import com.github.ezauton.core.localization.RotationalLocationEstimator
import com.github.ezauton.core.localization.TankRobotVelocityEstimator
import com.github.ezauton.core.localization.TranslationalLocationEstimator
import com.github.ezauton.core.localization.Updatable
import com.github.ezauton.core.localization.sensors.TranslationalDistanceSensor
import com.github.ezauton.core.record.Data
import com.github.ezauton.core.record.Sampler
import com.github.ezauton.core.robot.TankRobotConstants
import com.github.ezauton.core.utils.math.getAbsoluteDPosCurve
import com.github.ezauton.core.utils.math.getAngularDistance
import com.github.ezauton.core.utils.math.polarVector2D




/**
 * Describes an object that can estimate the heading and absolute position of the robot solely using the encoders
 */
class TankRobotEncoderEncoderEstimator
/**
 * Create a TankRobotEncoderEstimator
 *
 * @param left A reference to the encoder on the left side of the robot
 * @param right A reference to the encoder on the right side of the robot
 * @param tankRobot A reference to an object containing data about the structure of the drivetrain
 */
  (
  private val left: TranslationalDistanceSensor,
  private val right: TranslationalDistanceSensor,
  private val tankRobot: TankRobotConstants
) : RotationalLocationEstimator, TranslationalLocationEstimator, TankRobotVelocityEstimator, Updatable, Sampler<Data.TREE> {

  private var lastPosLeft: Distance = zero()
  private var lastPosRight: Distance = zero()
  private var init = false
  private var heading: Angle = zero()
  private var location: ConcreteVector<Distance> = origin(2)

  override val leftTranslationalWheelVelocity: LinearVelocity get() = left.velocity

  override val rightTranslationalWheelVelocity: LinearVelocity get() = right.velocity


  /**
   * Reset the heading and position of the location estimator
   */
  fun reset() {
    lastPosLeft = left.position
    lastPosRight = right.position
    location = origin(2)
    heading = zero()
    init = true
  }

  override fun estimateHeading(): Angle {
    return heading
  }

  override fun estimateLocation(): ConcreteVector<Distance> {
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

    val dLocation: ConcreteVector<Distance> = getAbsoluteDPosCurve(dl.value, dr.value, tankRobot.lateralWheelDistance.value, heading).withUnit()

    if (!dLocation.isFinite) {
      throw IllegalStateException("dLocation is $dLocation, which is not finite! dl = $dl, dr = $dr, heading = $heading")
    }

    location += dLocation
    heading += getAngularDistance(dl.value, dr.value, tankRobot.lateralWheelDistance.value).withUnit()
    return true
  }

  /**
   * @return The current velocity vector of the robot in 2D space.
   */
  override fun estimateAbsoluteVelocity(): ConcreteVector<LinearVelocity> {
    return polarVector2D(magnitude = avgTranslationalWheelVelocity, angle = heading)
  }

  override fun sample() = Data.TREE(leftTranslationalWheelVelocity, rightTranslationalWheelVelocity, heading, location.scalarVector)
}
