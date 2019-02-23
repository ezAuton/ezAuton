package com.github.ezauton.core.robot;

/**
 * Physical constants of a tank-drive robot.
 */
public interface TankRobotConstants extends RobotConstants {
    /**
     * @return How far apart the left wheels are from the right wheels.
     */
    double getLateralWheelDistance();
}
