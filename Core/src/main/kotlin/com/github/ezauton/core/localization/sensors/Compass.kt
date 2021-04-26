package com.github.ezauton.core.localization.sensors

import com.github.ezauton.conversion.Angle
import com.github.ezauton.conversion.SIUnit

/**
 * A CCW rotational censor on [0,360) which is 0 when facing forward, 90 when facing west, ...
 */
interface Compass : Sensor {
  /**
   * @return Robot angle in degrees. In front of robot is 0. To left is 90, behind is 180, to right is 270, top is 0
   */
  val angle: SIUnit<Angle>
}
