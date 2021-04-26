package com.github.ezauton.core.localization

import com.github.ezauton.conversion.*
import com.github.ezauton.core.localization.sensors.VelocityEstimator
import kotlin.math.abs

/**
 * Interface for any class that knows how fast the wheels on either side of the robot are going, given that the robot has a tank drivetrain
 */
interface TankRobotVelocityEstimator : VelocityEstimator {

  /**
   * @return Velocity of the left wheel. Can be negative or positive.
   */
  val leftTranslationalWheelVelocity: SIUnit<Velocity>

  /**
   * @return Velocity of the right wheel. Can be negative or positive.
   */
  val rightTranslationalWheelVelocity: SIUnit<Velocity>

  /**
   * @return Average velocity of both wheels. This will be the tangential velocity of the robot
   * if it is a normal tank robot.
   */
  val avgTranslationalWheelVelocity: SIUnit<Velocity>
    get() = (leftTranslationalWheelVelocity + rightTranslationalWheelVelocity) / 2.0

  override val translationalVelocity: SIUnit<Velocity>
    get() = avgTranslationalWheelVelocity

  /**
   * @return The average wheel speed. NOTE: this will always be positive and can be non-zero even
   * if the robot has 0 translational velocity.
   */
  val avgTranslationalWheelSpeed
    get() = (abs(leftTranslationalWheelVelocity) + abs(rightTranslationalWheelVelocity)) / 2.0

  /**
   * @return The absolute velocity of the robot
   */
  fun estimateAbsoluteVelocity(): ConcreteVector<Velocity>
}
