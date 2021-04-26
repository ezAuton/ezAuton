package com.github.ezauton.core.localization.sensors

import com.github.ezauton.conversion.Angle

/**
 * A sensor which can record revolutions/s and revolutions as a distance
 */
interface RotationalDistanceSensor : Tachometer {
  /**
   * @return revolutions
   */
  val position: Angle
}
