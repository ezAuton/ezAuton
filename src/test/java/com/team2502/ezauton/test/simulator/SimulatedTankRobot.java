package com.team2502.ezauton.test.simulator;

import com.team2502.ezauton.actuators.BoundedSFSimMotor;
import com.team2502.ezauton.actuators.StaticFrictionSimulatedMotor;
import com.team2502.ezauton.robot.ITankRobotConstants;
import com.team2502.ezauton.utils.ICopyableStopwatch;

public class SimulatedTankRobot implements ITankRobotConstants
{

    public static final double NORM_DT = 0.02D;

    private final double lateralWheelDistance;

    private final StaticFrictionSimulatedMotor leftMotor;
    private final StaticFrictionSimulatedMotor rightMotor;

    /**
     *
     * @param lateralWheelDistance The lateral wheel distance between the wheels of the robot
     * @param stopwatch The stopwatch that the simulated tank robot is using
     * @param maxAccel The max acceleration of the motors
     * @param minVel The minimum velocity the robot can continuously drive at (i.e. the robot cannot drive at 0.0001 ft/s)
     */
    public SimulatedTankRobot(double lateralWheelDistance, ICopyableStopwatch stopwatch, double maxAccel, double minVel, double maxVel)
    {
        // can accelerate 14 ft / s^2
        leftMotor = new BoundedSFSimMotor(stopwatch.copy(), maxAccel, minVel, maxVel);
        rightMotor = new BoundedSFSimMotor(stopwatch.copy(), maxAccel, minVel, maxVel);
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
