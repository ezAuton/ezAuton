package com.github.ezauton.wpilib.sensors

import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.buttons.JoystickButton
import com.github.ezauton.core.utils.EzJoystickButton
import com.github.ezauton.wpilib.command.CommandCreator
import com.github.ezauton.core.actuators.VelocityMotor
import com.github.ezauton.core.localization.sensors.RotationalDistanceSensor
import com.github.ezauton.core.actuators.VoltageMotor
import com.ctre.phoenix.motorcontrol.can.BaseMotorController
import com.github.ezauton.wpilib.motors.TypicalMotor
import com.ctre.phoenix.motorcontrol.ControlMode
import java.util.Arrays
import edu.wpi.first.wpilibj.PIDController
import edu.wpi.first.wpilibj.SpeedController
import java.lang.Void
import java.lang.IllegalStateException
import com.kauailabs.navx.frc.AHRS
import com.github.ezauton.core.localization.sensors.Compass

object Gyros {
  fun fromNavx(navx: AHRS): Compass {
    return Compass {
      val angle = -navx.angle // we want CCW orientation
      var boundedAngle = angle % 360
      if (boundedAngle < 0) {
        boundedAngle = 360 + boundedAngle
      }
      boundedAngle
    }
  }
}
