package com.github.ezauton.wpilib.motors

import com.github.ezauton.core.localization.sensors.RotationalDistanceSensor
import com.ctre.phoenix.motorcontrol.can.BaseMotorController
import com.ctre.phoenix.motorcontrol.ControlMode
import java.util.Arrays
import edu.wpi.first.wpilibj.PIDController
import edu.wpi.first.wpilibj.SpeedController
import edu.wpi.first.wpilibj.Encoder
import java.util.function.Consumer

/**
 * Utility class for converting WPILib motor controllers into
 */
object MotorControllers {
  /**
   * Create a combo VelocityMotor, Encoder, and VoltageMotor from a CTRE motor
   *
   * @param motorController The instance of the motor
   * @param pidIdx          0 for normal PID, 1 for auxiliary PID.
   * @return An [TypicalMotor]
   */
  fun fromCTRE(motorController: BaseMotorController, pidIdx: Int): TypicalMotor {
    return object : TypicalMotor {
      override fun runVoltage(targetVoltage: Double) {
        motorController[ControlMode.PercentOutput] = targetVoltage
      }

      fun runVelocity(targetVelocity: Double) {
        motorController[ControlMode.Velocity] = targetVelocity
      }

      override val position: Double
        get() = motorController.getSelectedSensorPosition(pidIdx).toDouble()
      override val velocity: Double
        get() = motorController.getSelectedSensorVelocity(pidIdx).toDouble()
    }
  }

  fun fromSeveralCTRE(master: BaseMotorController, pidIdx: Int, vararg slaves: BaseMotorController?): TypicalMotor {
    return object : TypicalMotor {
      fun runVelocity(targetVelocity: Double) {
        makeSlavesFollowMaster()
        master[ControlMode.Velocity] = targetVelocity
      }

      override fun runVoltage(targetVoltage: Double) {
        makeSlavesFollowMaster()
        master[ControlMode.PercentOutput] = targetVoltage
      }

      override val position: Double
        get() = master.getSelectedSensorPosition(pidIdx).toDouble()
      override val velocity: Double
        get() = master.getSelectedSensorVelocity(pidIdx).toDouble()

      private fun makeSlavesFollowMaster() {
        Arrays.stream(slaves).forEach(Consumer { s: BaseMotorController -> s.follow(master) })
      }
    }
  }

  fun fromWPILibEncoder(encoder: Encoder): RotationalDistanceSensor {
    return object : RotationalDistanceSensor {
      override val position: Double
        get() = encoder.distance
      override val velocity: Double
        get() = encoder.rate
    }
  }

  fun fromPWM(controller: PIDController, encoder: Encoder, motor: SpeedController): TypicalMotor {
    return object : TypicalMotor {
      override fun runVoltage(targetVoltage: Double) {
        controller.disable()
        motor.set(targetVoltage)
      }

      fun runVelocity(targetVelocity: Double) {
        if (!controller.isEnabled) {
          controller.enable()
        }
        controller.setpoint = targetVelocity
      }

      override val position: Double
        get() = encoder.distance
      override val velocity: Double
        get() = encoder.rate
    }
  }
}
