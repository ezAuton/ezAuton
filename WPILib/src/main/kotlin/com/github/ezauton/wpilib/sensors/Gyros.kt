package com.github.ezauton.wpilib.sensors

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
