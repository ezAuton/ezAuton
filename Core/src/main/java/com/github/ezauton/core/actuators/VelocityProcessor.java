package com.github.ezauton.core.actuators;

/**
 * A class which takes in a velocity, and automatically applies the velocity to another motor
 * This is nice for grouping multiple velocity processors on top of each other. Primarily used for simulated motors.
 */
public abstract class VelocityProcessor implements IVelocityMotor {
    private final IVelocityMotor toApply;

    public VelocityProcessor(IVelocityMotor toApply) {
        this.toApply = toApply;
    }

    public IVelocityMotor getToApply() {
        return toApply;
    }

}
