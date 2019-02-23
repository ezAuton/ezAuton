package com.github.ezauton.wpilib.motors;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.github.ezauton.core.localization.sensors.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.SpeedController;

import java.util.Arrays;

/**
 * Utility class for converting WPILib motor controllers into
 */
public class MotorControllers {
    /**
     * Create a combo VelocityMotor, Encoder, and VoltageMotor from a CTRE motor
     *
     * @param motorController The instance of the motor
     * @param pidIdx          0 for normal PID, 1 for auxiliary PID.
     * @return An {@link TypicalMotor}
     */
    public static TypicalMotor fromCTRE(BaseMotorController motorController, int pidIdx) {
        return new TypicalMotor() {
            @Override
            public void runVoltage(double targetVoltage) {
                motorController.set(ControlMode.PercentOutput, targetVoltage);
            }

            @Override
            public void runVelocity(double targetVelocity) {
                motorController.set(ControlMode.Velocity, targetVelocity);
            }

            @Override
            public double getPosition() {
                return motorController.getSelectedSensorPosition(pidIdx);
            }

            @Override
            public double getVelocity() {
                return motorController.getSelectedSensorVelocity(pidIdx);
            }
        };
    }

    public static TypicalMotor fromSeveralCTRE(BaseMotorController master, int pidIdx, BaseMotorController... slaves) {
        return new TypicalMotor() {
            @Override
            public void runVelocity(double targetVelocity) {
                makeSlavesFollowMaster();
                master.set(ControlMode.Velocity, targetVelocity);
            }

            @Override
            public void runVoltage(double targetVoltage) {
                makeSlavesFollowMaster();
                master.set(ControlMode.PercentOutput, targetVoltage);
            }

            @Override
            public double getPosition() {
                return master.getSelectedSensorPosition(pidIdx);
            }

            @Override
            public double getVelocity() {
                return master.getSelectedSensorVelocity(pidIdx);
            }

            private void makeSlavesFollowMaster() {
                Arrays.stream(slaves).forEach(s -> s.follow(master));
            }

        };
    }

    public static Encoder fromWPILibEncoder(edu.wpi.first.wpilibj.Encoder encoder) {
        return new Encoder() {
            @Override
            public double getPosition() {
                return encoder.getDistance();
            }

            @Override
            public double getVelocity() {
                return encoder.getRate();
            }
        };
    }


    public static TypicalMotor fromPWM(PIDController controller, edu.wpi.first.wpilibj.Encoder encoder, SpeedController motor) {
        return new TypicalMotor() {
            @Override
            public void runVoltage(double targetVoltage) {
                controller.disable();
                motor.set(targetVoltage);
            }

            @Override
            public void runVelocity(double targetVelocity) {
                if (!controller.isEnabled()) {
                    controller.enable();
                }
                controller.setSetpoint(targetVelocity);
            }

            @Override
            public double getPosition() {
                return encoder.getDistance();
            }

            @Override
            public double getVelocity() {
                return encoder.getRate();
            }
        };
    }
}
