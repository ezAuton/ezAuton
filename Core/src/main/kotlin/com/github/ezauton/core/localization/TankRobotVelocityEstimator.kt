package com.github.ezauton.core.localization

import com.github.ezauton.conversion.ConcreteVector
import com.github.ezauton.conversion.LinearVelocity
import com.github.ezauton.core.localization.sensors.VelocityEst

/**
 * Interface for any class that knows how fast the wheels on either side of the robot are going, given that the robot has a tank drivetrain
 */
interface TankRobotVelocityEstimator : VelocityEst {

  /**
   * @return Velocity of the left wheel. Can be negative or positive.
   */
  val leftTranslationalWheelVelocity: LinearVelocity

  /**
   * @return Velocity of the right wheel. Can be negative or positive.
   */
  val rightTranslationalWheelVelocity: LinearVelocity

  /**
   * @return Average velocity of both wheels. This will be the tangential velocity of the robot
   * if it is a normal tank robot.
   */
  val avgTranslationalWheelVelocity: LinearVelocity
    get() = (leftTranslationalWheelVelocity + rightTranslationalWheelVelocity) / 2.0

  override val translationalVelocity: LinearVelocity
    get() = avgTranslationalWheelVelocity

  /**
   * @return The average wheel speed. NOTE: this will always be positive and can be non-zero even
   * if the robot has 0 translational velocity.
   */
  val avgTranslationalWheelSpeed
    get() = (leftTranslationalWheelVelocity.abs() + rightTranslationalWheelVelocity.abs()) / 2.0

  /**
   * @return The absolute velocity of the robot
   */
  fun estimateAbsoluteVelocity(): ConcreteVector<LinearVelocity>
}
