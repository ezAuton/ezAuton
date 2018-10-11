package org.github.ezauton.ezauton.robot;

/**
 * Physical constants of a tank-drive robot.
 */
public interface ITankRobotConstants extends IRobotConstants
{
    /**
     * @return How far apart the left wheels are from the right wheels.
     */
    double getLateralWheelDistance();
}
