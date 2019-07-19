package com.github.ezauton.ftc.utility;

import com.github.ezauton.core.actuators.TypicalMotor;
import com.qualcomm.robotcore.hardware.DcMotor;

public class MotorControllers {
    public static TypicalMotor fromDcMotor(DcMotor motor) {
        return new TypicalMotor() {
            @Override
            public void runVelocity(double targetVelocity) {
                motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                motor.setPower(targetVelocity);
            }

            @Override
            public void runVoltage(double targetVoltage) {
                motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                motor.setPower(targetVoltage);
            }

            @Override
            public double getPosition() {
                return motor.getCurrentPosition();
            }

            @Override
            public double getVelocity() {
                return -1;
            }
        };
    }
}
