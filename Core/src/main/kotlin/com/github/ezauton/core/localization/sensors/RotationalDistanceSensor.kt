package com.github.ezauton.core.localization.sensors

/**
 * A sensor which can record revolutions/s and revolutions as a distance
 */
interface RotationalDistanceSensor : Tachometer {
  /**
   * @return revolutions
   */
  val position: Double
}
