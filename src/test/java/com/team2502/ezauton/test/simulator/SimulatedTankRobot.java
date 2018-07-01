package com.team2502.ezauton.test.simulator;

import com.team2502.ezauton.actuators.*;
import com.team2502.ezauton.localization.sensors.EncoderWheel;
import com.team2502.ezauton.localization.sensors.Encoders;
import com.team2502.ezauton.robot.ITankRobotConstants;
import com.team2502.ezauton.utils.SimulatedStopwatch;

public class SimulatedTankRobot implements ITankRobotConstants
{

    public static final double NORM_DT = 0.02D;

    private final double lateralWheelDistance;

    private final EncoderWheel left;
    private final EncoderWheel right;
    private final StaticFrictionSimulatedMotor leftMotor;
    private final StaticFrictionSimulatedMotor rightMotor;

    public SimulatedTankRobot(double lateralWheelDistance, double wheelSize, double dt)
    {
        SimulatedStopwatch stopwatch = new SimulatedStopwatch(dt);
        leftMotor = new BoundedSFSimMotor(stopwatch, 0.2, 0.3,16);
        rightMotor = new BoundedSFSimMotor(stopwatch, 0.2, 0.3,16);
        this.lateralWheelDistance = lateralWheelDistance;

        left = new EncoderWheel(Encoders.fromTachometer(leftMotor, stopwatch.clone()), wheelSize);
        right = new EncoderWheel(Encoders.fromTachometer(rightMotor, stopwatch.clone()), wheelSize);
    }

    public StaticFrictionSimulatedMotor getLeftMotor()
    {
        return leftMotor;
    }

    public StaticFrictionSimulatedMotor getRightMotor()
    {
        return rightMotor;
    }

    public EncoderWheel getLeftWheel()
    {
        return left;
    }

    public EncoderWheel getRightWheel()
    {
        return right;
    }

    public double getLateralWheelDistance()
    {
        return lateralWheelDistance;
    }
}
