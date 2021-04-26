package com.github.ezauton.core.robot

/**
 * Physical constants of a tank-drive robot.
 */
interface TankRobotConstants : RobotConstants {
  /**
   * @return How far apart the left wheels are from the right wheels.
   */
  val lateralWheelDistance: Double
}
