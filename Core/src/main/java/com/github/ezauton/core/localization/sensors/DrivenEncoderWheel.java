package com.github.ezauton.core.localization.sensors;

import com.github.ezauton.core.actuators.VelocityMotor;

public class DrivenEncoderWheel extends EncoderWheel {

    private final VelocityMotor motor;

    /**
     * @param rotationalDistanceSensor
     * @param wheelDiameter            The diameter of the wheel with the encoder (recommended in ft)
     */
    public DrivenEncoderWheel(RotationalDistanceSensor rotationalDistanceSensor, double wheelDiameter, double timeMultiplier, VelocityMotor motor) {
        super(rotationalDistanceSensor, wheelDiameter, timeMultiplier);
        this.motor = motor;
    }

    @Deprecated
    public void setVelocity(double linearVelocity) {
        double desiredRotVel = linearVelocity / ((Math.PI * getWheelDiameter()) * getDistanceMultiplier() * getTimeMultiplier());
        motor.runAngularVelocity(desiredRotVel);
    }

    public void setLinearVelocity(double velocity) {
        setVelocity(velocity);
    }
}
