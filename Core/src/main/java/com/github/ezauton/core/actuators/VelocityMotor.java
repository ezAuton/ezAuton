package com.github.ezauton.core.actuators;

/**
 * A motor which can be run at a certain velocity
 */
public interface VelocityMotor extends Motor {
    /**
     * Run the motor at a certain velocity
     *
     * @param targetVelocity The speed to run the motor at
     */
    void runVelocity(double targetVelocity);
}
