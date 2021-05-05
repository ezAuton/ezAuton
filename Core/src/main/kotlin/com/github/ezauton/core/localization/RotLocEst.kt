package com.github.ezauton.core.localization

import com.github.ezauton.conversion.Angle

/**
 * An interface for any class trying to estimate our heading
 */
interface RotLocEst {
  /**
   * @return The estimated heading of the robot in radians, where 0 is N, pi/2 is W...
   */
  fun estimateHeading(): Angle
}
