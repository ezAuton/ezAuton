package com.team2502.ezauton.test.simulator;

import com.team2502.ezauton.actuators.IVelocityMotor;
import com.team2502.ezauton.actuators.implementations.BaseSimulatedMotor;
import com.team2502.ezauton.actuators.implementations.BoundedVelocityProcessor;
import com.team2502.ezauton.actuators.implementations.RampUpVelocityProcessor;
import com.team2502.ezauton.actuators.implementations.StaticFrictionVelocityProcessor;
import com.team2502.ezauton.localization.Updateable;
import com.team2502.ezauton.localization.UpdateableGroup;
import com.team2502.ezauton.localization.sensors.ITranslationalDistanceSensor;
import com.team2502.ezauton.robot.ITankRobotConstants;
import com.team2502.ezauton.utils.ICopyableStopwatch;

public class SimulatedTankRobot implements ITankRobotConstants, Updateable
{

    public static final double NORM_DT = 0.02D;

    private final double lateralWheelDistance;

    private final IVelocityMotor leftMotor;
    private final IVelocityMotor rightMotor;

    private final BaseSimulatedMotor baseLeftSimulatedMotor;
    private final BaseSimulatedMotor baseRightSimulatedMotor;

    private UpdateableGroup toUpdate = new UpdateableGroup();

    /**
     * @param lateralWheelDistance The lateral wheel distance between the wheels of the robot
     * @param stopwatch            The stopwatch that the simulated tank robot is using
     * @param maxAccel             The max acceleration of the motors
     * @param minVel               The minimum velocity the robot can continuously drive at (i.e. the robot cannot drive at 0.0001 ft/s)
     */
    public SimulatedTankRobot(double lateralWheelDistance, ICopyableStopwatch stopwatch, double maxAccel, double minVel, double maxVel)
    {
        baseLeftSimulatedMotor = new BaseSimulatedMotor(stopwatch.copy());
        this.leftMotor = buildMotor(baseLeftSimulatedMotor, stopwatch, maxAccel, minVel, maxVel);

        baseRightSimulatedMotor = new BaseSimulatedMotor(stopwatch.copy());
        this.rightMotor = buildMotor(baseRightSimulatedMotor, stopwatch, maxAccel, minVel, maxVel);

        this.lateralWheelDistance = lateralWheelDistance;

    }

    public IVelocityMotor getLeftMotor()
    {
        return leftMotor;
    }

    public IVelocityMotor getRightMotor()
    {
        return rightMotor;
    }

    public void run(double left, double right)
    {
        leftMotor.runVelocity(left);
        rightMotor.runVelocity(right);
    }

    private IVelocityMotor buildMotor(BaseSimulatedMotor baseSimulatedMotor, ICopyableStopwatch stopwatch, double maxAccel, double minVel, double maxVel)
    {
        RampUpVelocityProcessor leftRampUpMotor = new RampUpVelocityProcessor(baseSimulatedMotor, stopwatch.copy(), maxAccel);
        toUpdate.add(leftRampUpMotor);

        StaticFrictionVelocityProcessor leftSF = new StaticFrictionVelocityProcessor(baseSimulatedMotor, leftRampUpMotor, minVel);
        return new BoundedVelocityProcessor(leftSF, maxVel);
    }

    public ITranslationalDistanceSensor getLeftDistanceSensor()
    {
        return baseLeftSimulatedMotor;
    }

    public ITranslationalDistanceSensor getRightDistanceSensor()
    {
        return baseRightSimulatedMotor;
    }

    public double getLateralWheelDistance()
    {
        return lateralWheelDistance;
    }

    @Override
    public boolean update()
    {
        toUpdate.update();
        return true;
    }
}
