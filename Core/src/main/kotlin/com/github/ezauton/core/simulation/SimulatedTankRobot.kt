package com.github.ezauton.core.simulation

import com.github.ezauton.conversion.*
import com.github.ezauton.core.actuators.VelocityMotor
import com.github.ezauton.core.actuators.implementations.SimulatedMotor
import com.github.ezauton.core.localization.Updatable
import com.github.ezauton.core.localization.estimators.TankRobotEncoderEncoderEstimator
import com.github.ezauton.core.localization.sensors.Encoders
import com.github.ezauton.core.localization.sensors.TranslationalDistanceSensor
import com.github.ezauton.core.robot.TankRobotConstants
import com.github.ezauton.core.robot.implemented.TankRobotTransLocDrivable
import com.github.ezauton.core.utils.Clock
import com.github.ezauton.core.utils.Stopwatch
import java.time.Clock

class SimulatedTankRobot
/**
 * @param lateralWheelDistance The lateral wheel distance between the wheels of the robot
 * @param clock The clock that the simulated tank robot is using
 * @param maxAccel The max acceleration of the motors
 * @param minVel The minimum velocity the robot can continuously drive at (i.e. the robot cannot drive at 0.0001 ft/s)
 */
  (override val lateralWheelDistance: Distance, maxAccel: LinearAcceleration, minVel: LinearVelocity, maxVel: LinearVelocity) : TankRobotConstants, Updatable {

  private val left: SimulatedMotor
  private val right: SimulatedMotor

  private val stopwatch: Stopwatch = Stopwatch.start()
  val leftDistanceSensor: TranslationalDistanceSensor
  val rightDistanceSensor: TranslationalDistanceSensor

  /**
   * @return A location estimator which automatically updates
   */
  val locationEstimator: TankRobotEncoderEncoderEstimator
  val driving: TankRobotTransLocDrivable

  //    public StringBuilder log = new StringBuilder("t, v_l, v_r\n");
  private val toUpdate: Set<Updatable>

  val leftMotor: VelocityMotor get() = left
  val rightMotor: VelocityMotor get() = right

  init {

    val maxAccelAngular: AngularAcceleration = SI(maxAccel.value)
    val minVelAngular: AngularVelocity = SI(minVel.value)
    val maxVelAngular: AngularVelocity = SI(maxVel.value)

    left = SimulatedMotor(maxAccelAngular, minVelAngular, maxVelAngular, 1.0)
    leftDistanceSensor = Encoders.toTranslationalDistanceSensor(1.0.meters, 1.0.mps, left)

    right = SimulatedMotor(maxAccelAngular, minVelAngular, maxVelAngular, 1.0)
    rightDistanceSensor = Encoders.toTranslationalDistanceSensor(1.0.meters, 1.0.mps, right)

    toUpdate = setOf(left, right)

    this.locationEstimator = TankRobotEncoderEncoderEstimator(leftDistanceSensor, rightDistanceSensor, this).apply { reset() }
    this.driving = TankRobotTransLocDrivable(left, right, locationEstimator, locationEstimator, this)
  }

  fun run(leftV: LinearVelocity, rightV: LinearVelocity) {
    update()
    left.runVelocity(leftV.s.withUnit())
    right.runVelocity(rightV.s.withUnit())
  }

  override fun update(): Boolean {
    toUpdate.forEach { it.update() }
    locationEstimator.update()
    return true
  }
}
