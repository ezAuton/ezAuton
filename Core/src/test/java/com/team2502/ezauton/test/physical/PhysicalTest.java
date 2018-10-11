package com.team2502.ezauton.test.physical;

import com.team2502.ezauton.actuators.IVelocityMotor;
import com.team2502.ezauton.actuators.IVoltageMotor;
import com.team2502.ezauton.command.*;
import com.team2502.ezauton.localization.estimators.TankRobotEncoderEncoderEstimator;
import com.team2502.ezauton.localization.sensors.ITranslationalDistanceSensor;

import java.util.concurrent.TimeUnit;

public class PhysicalTest
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
    public static IAction testStraightVoltage(IVoltageMotor leftMotor, IVoltageMotor rightMotor, double voltage)
    {
        // run for 5 seconds
        return new PeriodicAction(TimeUnit.MILLISECONDS, 20)
        {
            @Override
            public void execute()
            {
                leftMotor.runVoltage(voltage);
                rightMotor.runVoltage(voltage);
            }

            @Override
            protected boolean isFinished()
            {
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
    public static IAction testStraightVelocity(IVelocityMotor leftMotor, IVelocityMotor rightMotor, double velocity)
    {
        // run for 5 seconds
        return new PeriodicAction(TimeUnit.MILLISECONDS, 20)
        {
            @Override
            public void execute()
            {
                leftMotor.runVelocity(velocity);
                rightMotor.runVelocity(velocity);
            }

            @Override
            protected boolean isFinished()
            {
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
    public static IAction testStraightEncoderEncoderLocalization(ITranslationalDistanceSensor left, ITranslationalDistanceSensor right, IVoltageMotor leftMotor, IVoltageMotor rightMotor, double lateralWheelDistance, double voltage)
    {
        IAction action = testStraightVoltage(leftMotor, rightMotor, voltage);
        TankRobotEncoderEncoderEstimator localizer = new TankRobotEncoderEncoderEstimator(left, right, () -> lateralWheelDistance);
        localizer.reset();
        return new ActionGroup().with(new BackgroundAction(TimeUnit.MILLISECONDS, 50, localizer))
                                .addSequential(action)
                                .addSequential(new BaseAction(() -> System.out.println(localizer.estimateLocation())));
    }
}
