package com.github.ezauton.core.physical

import com.github.ezauton.conversion.AngularVelocity
import com.github.ezauton.conversion.seconds
import com.github.ezauton.core.action.action
import com.github.ezauton.core.action.periodic
import com.github.ezauton.core.actuators.RotVelMotor
import com.github.ezauton.core.actuators.VoltageMotor


object PhysicalTest { // TODO: what we using this for

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
  fun testStraightVoltage(leftMotor: VoltageMotor, rightMotor: VoltageMotor, voltage: Double) = action {
    periodic(duration = 5.seconds) {
      leftMotor.runVoltage(voltage)
      rightMotor.runVoltage(voltage)
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
  fun testStraightVelocity(leftMotor: RotVelMotor, rightMotor: RotVelMotor, velocity: AngularVelocity) = action {
    periodic(duration = 5.seconds) {
      leftMotor.runVelocity(velocity)
      rightMotor.runVelocity(velocity)
    }
  }

//  /**
//   * Test if encoder-encoder localization when going straight works
//   *
//   * @param left
//   * @param right
//   * @param leftMotor
//   * @param rightMotor
//   * @param lateralWheelDistance
//   * @param voltage
//   * @return
//   */
//  fun testStraightEncoderEncoderLocalization(
//    left: TranslationalDistanceSensor,
//    right: TranslationalDistanceSensor,
//    leftMotor: VoltageMotor,
//    rightMotor: VoltageMotor,
//    lateralWheelDistance: Distance,
//    voltage: Double
//  ): Action<*> {
//    val action = testStraightVoltage(leftMotor, rightMotor, voltage)
//    val constraints = TankRobotConstants.from(lateralWheelDistance)
//    val localizer = TankRobotEncoderEncoderEstimator(left, right, constraints)
//    localizer.reset()
//
//    return action {
//      with {
//        periodic {
//          localizer.update()
//        }
//      }
//      sequential(action)
//    }
//
//  }
}
