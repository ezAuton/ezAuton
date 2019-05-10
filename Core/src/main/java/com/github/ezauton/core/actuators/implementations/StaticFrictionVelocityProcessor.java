package com.github.ezauton.core.actuators.implementations;

import com.github.ezauton.core.actuators.VelocityMotor;
import com.github.ezauton.core.actuators.VelocityProcessor;
import com.github.ezauton.core.localization.sensors.TranslationalDistanceSensor;

/**
 * A velocity processor that does not run the motor if the target velocity is less than the minimum velocity that is required to move.
 */
public class StaticFrictionVelocityProcessor extends VelocityProcessor {

    private final double minVelMove;
    private final TranslationalDistanceSensor distanceSensor;

    /**
     * Create a StaticFrictionVelocityProcessor
     *
     * @param distanceSensor An encoder
     * @param toApply        The motor to apply the processed velocity to
     * @param minVelMove     The minimum velocity to move the motor
     */
    public StaticFrictionVelocityProcessor(TranslationalDistanceSensor distanceSensor, VelocityMotor toApply, double minVelMove) {
        super(toApply);
        this.minVelMove = minVelMove;
        this.distanceSensor = distanceSensor;
    }

    /**
     * Run the motor at the target velocity, unless the target velocity is too small and we are not moving
     *
     * @param targetVelocity The speed to run the motor at
     */
    @Override
    public void runVelocity(double targetVelocity) {
        if (distanceSensor.getVelocity() != 0 || Math.abs(targetVelocity) >= minVelMove) {
            getToApply().runVelocity(targetVelocity);
        }
    }
}
