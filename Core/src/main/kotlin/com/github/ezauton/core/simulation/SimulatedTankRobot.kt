package com.github.ezauton.core.simulation

import com.github.ezauton.conversion.*
import com.github.ezauton.core.actuators.implementations.SimulatedMotor
import com.github.ezauton.core.localization.RotLocEst
import com.github.ezauton.core.localization.TankRobotVelEst
import com.github.ezauton.core.localization.TransLocEst
import com.github.ezauton.core.localization.Updatable
import com.github.ezauton.core.localization.estimators.TankRobotEncoderEncoderEstimator
import com.github.ezauton.core.localization.sensors.Encoders
import com.github.ezauton.core.localization.sensors.TranslationalDistanceSensor
import com.github.ezauton.core.record.Data
import com.github.ezauton.core.record.Sampler
import com.github.ezauton.core.robot.TankRobotConstants
import com.github.ezauton.core.robot.implemented.TankRobotTransLocDrivable
import com.github.ezauton.core.robot.subsystems.TransLocDrivable

class SimulatedTankRobot
/**
 * @param lateralWheelDistance The lateral wheel distance between the wheels of the robot
 * @param clock The clock that the simulated tank robot is using
 * @param maxAccel The max acceleration of the motors
 * @param minVel The minimum velocity the robot can continuously drive at (i.e. the robot cannot drive at 0.0001 ft/s)
 */
private constructor(
  private val constraints: TankRobotConstants,
  val leftMotor: SimulatedMotor,
  val rightMotor: SimulatedMotor,
  private val locationEstimator: TankRobotEncoderEncoderEstimator,
  private val driving: TransLocDrivable,
  val leftDistanceSensor: TranslationalDistanceSensor,
  val rightDistanceSensor: TranslationalDistanceSensor,
) :
  TankRobotConstants by constraints,
  RotLocEst by locationEstimator,
  TankRobotVelEst by locationEstimator,
  TransLocEst by locationEstimator,
  Sampler<Data.TREE> by locationEstimator,
  TransLocDrivable,
  Updatable {

  companion object {
    fun create(lateralWheelDistance: Distance, maxAccel: LinearAcceleration, minVel: LinearVelocity, maxVel: LinearVelocity): SimulatedTankRobot {
      val constraints = TankRobotConstants.from(lateralWheelDistance)

      val maxAccelAngular: AngularAcceleration = SI(maxAccel.value)
      val minVelAngular: AngularVelocity = SI(minVel.value)
      val maxVelAngular: AngularVelocity = SI(maxVel.value)
      val leftMotor = SimulatedMotor(maxAccelAngular, minVelAngular, maxVelAngular, 1.0)
      val rightMotor = SimulatedMotor(maxAccelAngular, minVelAngular, maxVelAngular, 1.0)

      val leftDistanceSensor = Encoders.toTranslationalDistanceSensor(1.0.meters, 1.0.mps, leftMotor)
      val rightDistanceSensor = Encoders.toTranslationalDistanceSensor(1.0.meters, 1.0.mps, rightMotor)
      val locationEstimator = TankRobotEncoderEncoderEstimator.from(leftDistanceSensor, rightDistanceSensor, constraints)
      val driving = TankRobotTransLocDrivable(leftMotor, rightMotor, locationEstimator, locationEstimator, constraints)
      return SimulatedTankRobot(constraints, leftMotor, rightMotor, locationEstimator, driving, leftDistanceSensor, rightDistanceSensor)
    }
  }

  override fun estimateAbsoluteVelocity() = locationEstimator.estimateAbsoluteVelocity()



  /**
   * @return A location estimator which automatically updates
   */

  private val toUpdate = listOf(leftMotor, rightMotor)

  fun run(leftV: LinearVelocity, rightV: LinearVelocity) {
    update()
    leftMotor.runVelocity(leftV.s.withUnit())
    rightMotor.runVelocity(rightV.s.withUnit())
  }

  override fun update(): Boolean {
    toUpdate.forEach { it.update() }
    locationEstimator.update()
    return true
  }

  override fun driveTowardTransLoc(speed: LinearVelocity, loc: ConcreteVector<Distance>) = run {
    update()
    driving.driveTowardTransLoc(speed, loc)
  }

  override fun driveSpeed(speed: LinearVelocity): Boolean = run {
    update()
    driving.driveSpeed(speed)
  }
}
