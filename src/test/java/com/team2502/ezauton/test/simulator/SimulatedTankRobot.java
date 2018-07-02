package com.team2502.ezauton.test.simulator;

import com.team2502.ezauton.actuators.BoundedSFSimMotor;
import com.team2502.ezauton.actuators.StaticFrictionSimulatedMotor;
import com.team2502.ezauton.robot.ITankRobotConstants;
import com.team2502.ezauton.utils.SimulatedStopwatch;

public class SimulatedTankRobot implements ITankRobotConstants
{

    public static final double NORM_DT = 0.02D;

    private final double lateralWheelDistance;

    private final StaticFrictionSimulatedMotor leftMotor;
    private final StaticFrictionSimulatedMotor rightMotor;

    public SimulatedTankRobot(double lateralWheelDistance, double wheelSize, SimulatedStopwatch stopwatch)
    {
        leftMotor = new BoundedSFSimMotor(stopwatch.copy(), 1, 0.3, 16);
        rightMotor = new BoundedSFSimMotor(stopwatch.copy(), 1, 0.3, 16);
        this.lateralWheelDistance = lateralWheelDistance;
    }

    public StaticFrictionSimulatedMotor getLeftMotor()
    {
        return leftMotor;
    }

    public StaticFrictionSimulatedMotor getRightMotor()
    {
        return rightMotor;
    }

    public void run(double left, double right)
    {
        leftMotor.runVelocity(left);
        rightMotor.runVelocity(right);
    }

    public double getLateralWheelDistance()
    {
        return lateralWheelDistance;
    }
}
