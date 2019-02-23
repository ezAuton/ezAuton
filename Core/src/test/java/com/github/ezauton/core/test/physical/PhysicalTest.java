package com.github.ezauton.core.test.physical;

import com.github.ezauton.core.action.*;
import com.github.ezauton.core.actuators.VelocityMotor;
import com.github.ezauton.core.actuators.VoltageMotor;
import com.github.ezauton.core.localization.estimators.TankRobotEncoderEncoderEstimator;
import com.github.ezauton.core.localization.sensors.TranslationalDistanceSensor;

import java.util.concurrent.TimeUnit;

public class PhysicalTest // TODO: what we using this for
{
    /**
     * Test if the robot goes straight forward with constant voltage for two motors
     * <br><br>
     * If performs a 0-point turn one of the polarities is wrong
     * <br>
     * If goes backwards both polarities are wrong
     *
     * @param leftMotor
     * @param rightMotor
     */
    public static Action testStraightVoltage(VoltageMotor leftMotor, VoltageMotor rightMotor, double voltage) {
        // run for 5 seconds
        return new PeriodicAction(20, TimeUnit.MILLISECONDS) {
            @Override
            public void execute() {
                leftMotor.runVoltage(voltage);
                rightMotor.runVoltage(voltage);
            }

            @Override
            protected boolean isFinished() {
                return getStopwatch().read(TimeUnit.SECONDS) > 5;
            }
        };
    }

    /**
     * Test if the robot goes straight with constant velocity for two motors
     * <br><br>
     * If performs a 0-point turn one of the polarities is wrong
     * <br>
     * If goes backwards both polarities are wrong
     *
     * @param leftMotor
     * @param rightMotor
     */
    public static Action testStraightVelocity(VelocityMotor leftMotor, VelocityMotor rightMotor, double velocity) {
        // run for 5 seconds
        return new PeriodicAction(20, TimeUnit.MILLISECONDS) {
            @Override
            public void execute() {
                leftMotor.runVelocity(velocity);
                rightMotor.runVelocity(velocity);
            }

            @Override
            protected boolean isFinished() {
                return getStopwatch().read(TimeUnit.SECONDS) > 5;
            }
        };
    }

    /**
     * Test if encoder-encoder localization when going straight works
     *
     * @param left
     * @param right
     * @param leftMotor
     * @param rightMotor
     * @param lateralWheelDistance
     * @param voltage
     * @return
     */
    public static Action testStraightEncoderEncoderLocalization(TranslationalDistanceSensor left, TranslationalDistanceSensor right, VoltageMotor leftMotor, VoltageMotor rightMotor, double lateralWheelDistance, double voltage) {
        Action action = testStraightVoltage(leftMotor, rightMotor, voltage);
        TankRobotEncoderEncoderEstimator localizer = new TankRobotEncoderEncoderEstimator(left, right, () -> lateralWheelDistance);
        localizer.reset();
        return new ActionGroup().with(new BackgroundAction(50, TimeUnit.MILLISECONDS, localizer::update))
                .addSequential(action)
                .addSequential(new BaseAction(() -> {
                }));
    }
}
