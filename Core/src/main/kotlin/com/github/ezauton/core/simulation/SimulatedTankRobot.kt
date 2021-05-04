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

class SimulatedTankRobot
/**
 * @param lateralWheelDistance The lateral wheel distance between the wheels of the robot
 * @param clock The clock that the simulated tank robot is using
 * @param maxAccel The max acceleration of the motors
 * @param minVel The minimum velocity the robot can continuously drive at (i.e. the robot cannot drive at 0.0001 ft/s)
 */
  (override val lateralWheelDistance: Distance, clock: Clock, maxAccel: LinearAcceleration, minVel: LinearVelocity, maxVel: LinearVelocity) : TankRobotConstants, Updatable {

  private val left: SimulatedMotor
  private val right: SimulatedMotor

  private val stopwatch: Stopwatch = Stopwatch(clock)
  val leftDistanceSensor: TranslationalDistanceSensor
  val rightDistanceSensor: TranslationalDistanceSensor

  /**
   * @return A location estimator which automatically updates
   */
  val defaultLocEstimator: TankRobotEncoderEncoderEstimator
  val defaultTransLocDriveable: TankRobotTransLocDrivable

  //    public StringBuilder log = new StringBuilder("t, v_l, v_r\n");
  private val toUpdate: Set<Updatable>

  val leftMotor: VelocityMotor get() = left
  val rightMotor: VelocityMotor get() = right

  init {
    stopwatch.init()

    val maxAccelAngular: AngularAcceleration = SI(maxAccel.value)
    val minVelAngular: AngularVelocity = SI(minVel.value)
    val maxVelAngular: AngularVelocity = SI(maxVel.value)

    left = SimulatedMotor(clock, maxAccelAngular, minVelAngular, maxVelAngular, 1.0)
    leftDistanceSensor = Encoders.toTranslationalDistanceSensor(1.0.meters, 1.0.mps, left)

    right = SimulatedMotor(clock, maxAccelAngular, minVelAngular, maxVelAngular, 1.0)
    rightDistanceSensor = Encoders.toTranslationalDistanceSensor(1.0.meters, 1.0.mps, right)

    toUpdate = setOf(left, right)

    this.defaultLocEstimator = TankRobotEncoderEncoderEstimator(leftDistanceSensor, rightDistanceSensor, this).apply { reset() }
    this.defaultTransLocDriveable = TankRobotTransLocDrivable(left, right, defaultLocEstimator, defaultLocEstimator, this)
  }

  fun run(leftV: LinearVelocity, rightV: LinearVelocity) {
    update()
    left.runVelocity(leftV.s.withUnit())
    right.runVelocity(rightV.s.withUnit())
  }

  override fun update(): Boolean {
    toUpdate.forEach { it.update() }
    defaultLocEstimator.update()
    return true
  }
}
