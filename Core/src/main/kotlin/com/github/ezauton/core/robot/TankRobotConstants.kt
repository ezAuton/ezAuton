package com.github.ezauton.core.robot

import com.github.ezauton.conversion.Distance

/**
 * Physical constants of a tank-drive robot.
 */
interface TankRobotConstants : RobotConstants {
  /**
   * @return How far apart the left wheels are from the right wheels.
   */
  val lateralWheelDistance: Distance

  companion object {
    fun from(lateralWheelDistance: Distance): TankRobotConstants {
      return ConstraintImpl(lateralWheelDistance)
    }
  }
}

private class ConstraintImpl(override val lateralWheelDistance: Distance): TankRobotConstants
