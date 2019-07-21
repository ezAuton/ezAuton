package com.github.ezauton.ftc.utility;

import com.github.ezauton.core.actuators.TypicalMotor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class MotorControllers {

    /**
     * Turn a DcMotorEx/several of them into an ezAuton compatible object
     *
     * @param motors       All motors on the mechanism, which need to be run simultaneously
     * @param ticksPerFoot Number of ticks on the encoder that correspond to 1 foot of travel
     * @return A TypicalMotor for which runVelocity uses feet/second, and getPosition uses feet
     */
    public static TypicalMotor fromDcMotorEx(double ticksPerFoot, DcMotorEx... motors) {
        return new TypicalMotor() {
            @Override
            public void runVelocity(double feetPerSecond) {
                for (DcMotorEx motor : motors) {
                    motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    motor.setVelocity(feetPerSecond * ticksPerFoot);
                }
            }

            @Override
            public void runVoltage(double targetVoltage) {
                for (DcMotorEx motor : motors) {
                    motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    motor.setPower(targetVoltage);
                }

            }

            @Override
            public double getPosition() {
                int ticks = motors[0].getCurrentPosition();
                return ticks / ticksPerFoot;
            }

            @Override
            public double getVelocity() {
                double ticksPerSecond = motors[0].getVelocity();
                return ticksPerSecond / ticksPerFoot;
            }


        };
    }
}
