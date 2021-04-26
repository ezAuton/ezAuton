package com.github.ezauton.core.physical

import com.github.ezauton.core.action.Action
import com.github.ezauton.core.action.ActionGroup
import com.github.ezauton.core.action.PeriodicAction
import com.github.ezauton.core.actuators.VelocityMotor
import com.github.ezauton.core.actuators.VoltageMotor
import com.github.ezauton.core.localization.estimators.TankRobotEncoderEncoderEstimator
import com.github.ezauton.core.localization.sensors.TranslationalDistanceSensor
import java.util.concurrent.TimeUnit

object PhysicalTest // TODO: what we using this for {

/**
 * Test if the robot goes straight forward with constant voltage for two motors
 * <br></br><br></br>
 * If performs a 0-point turn one of the polarities is wrong
 * <br></br>
 * If goes backwards both polarities are wrong
 *
 * @param leftMotor
 * @param rightMotor
 */
fun testStraightVoltage(leftMotor: VoltageMotor, rightMotor: VoltageMotor, voltage: Double): Action {
  // run for 5 seconds
  return object : PeriodicAction(20, TimeUnit.MILLISECONDS) {
    override fun execute() {
      leftMotor.runVoltage(voltage)
      rightMotor.runVoltage(voltage)
    }

    override fun isFinished(): Boolean {
      return stopwatch.read(TimeUnit.SECONDS) > 5
    }
  }
}

/**
 * Test if the robot goes straight with constant velocity for two motors
 * <br></br><br></br>
 * If performs a 0-point turn one of the polarities is wrong
 * <br></br>
 * If goes backwards both polarities are wrong
 *
 * @param leftMotor
 * @param rightMotor
 */
fun testStraightVelocity(leftMotor: VelocityMotor, rightMotor: VelocityMotor, velocity: Double): Action {
  // run for 5 seconds
  return object : PeriodicAction(20, TimeUnit.MILLISECONDS) {
    override fun execute() {
      leftMotor.runVelocity(velocity)
      rightMotor.runVelocity(velocity)
    }

    override fun isFinished(): Boolean {
      return stopwatch.read(TimeUnit.SECONDS) > 5
    }
  }
}

/**
 * Test if encoder-encoder localization when going straight works
 *
 * @param left
 * @param right
 * @param leftMotor
 * @param rightMotor
 * @param lateralWheelDistance
 * @param voltage
 * @return
 */
fun testStraightEncoderEncoderLocalization(
  left: TranslationalDistanceSensor,
  right: TranslationalDistanceSensor,
  leftMotor: VoltageMotor,
  rightMotor: VoltageMotor,
  lateralWheelDistance: Double,
  voltage: Double
): Action {
  val action = testStraightVoltage(leftMotor, rightMotor, voltage)
  val localizer = TankRobotEncoderEncoderEstimator(left, right) { lateralWheelDistance }
  localizer.reset()
  return ActionGroup().with(BackgroundAction(50, TimeUnit.MILLISECONDS, Runnable { localizer.update() }))
    .addSequential(action)
    .addSequential(BaseAction({ }))
}
}
