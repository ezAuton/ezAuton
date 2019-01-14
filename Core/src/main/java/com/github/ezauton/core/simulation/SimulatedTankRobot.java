package com.github.ezauton.core.simulation;

import com.github.ezauton.core.actuators.IVelocityMotor;
import com.github.ezauton.core.actuators.implementations.*;
import com.github.ezauton.core.localization.Updateable;
import com.github.ezauton.core.localization.UpdateableGroup;
import com.github.ezauton.core.localization.sensors.Encoders;
import com.github.ezauton.core.localization.sensors.ITranslationalDistanceSensor;
import com.github.ezauton.core.robot.ITankRobotConstants;
import com.github.ezauton.core.utils.IClock;
import com.github.ezauton.core.utils.Stopwatch;

import java.util.concurrent.TimeUnit;

public class SimulatedTankRobot implements ITankRobotConstants, Updateable
{

    public static final double NORM_DT = 0.02D;

    private final double lateralWheelDistance;

    private final SimulatedMotor left;
    private final SimulatedMotor right;

    private final Stopwatch stopwatch;
    private final ITranslationalDistanceSensor leftTDS;
    private final ITranslationalDistanceSensor rightTDS;
    public StringBuilder log = new StringBuilder("t, v_l, v_r\n");
    private UpdateableGroup toUpdate = new UpdateableGroup();

    /**
     * @param lateralWheelDistance The lateral wheel distance between the wheels of the robot
     * @param clock                The clock that the simulated tank robot is using
     * @param maxAccel             The max acceleration of the motors
     * @param minVel               The minimum velocity the robot can continuously drive at (i.e. the robot cannot drive at 0.0001 ft/s)
     */
    public SimulatedTankRobot(double lateralWheelDistance, IClock clock, double maxAccel, double minVel, double maxVel)
    {
        stopwatch = new Stopwatch(clock);
        stopwatch.init();

        left = new SimulatedMotor(clock, maxAccel, minVel, maxVel, 1);
        leftTDS = Encoders.toTranslationalDistanceSensor(1, 1, left);

        right = new SimulatedMotor(clock, maxAccel, minVel, maxVel, 1);
        rightTDS = Encoders.toTranslationalDistanceSensor(1, 1, right);

        toUpdate.add(left);
        toUpdate.add(right);
        this.lateralWheelDistance = lateralWheelDistance;

    }


    public IVelocityMotor getLeftMotor()
    {
        return left;
    }

    public IVelocityMotor getRightMotor()
    {
        return right;
    }

    public void run(double leftV, double rightV)
    {
        left.runVelocity(leftV);
        right.runVelocity(rightV);
    }

    public ITranslationalDistanceSensor getLeftDistanceSensor()
    {
        return leftTDS;
    }

    public ITranslationalDistanceSensor getRightDistanceSensor()
    {
        return rightTDS;
    }

    public double getLateralWheelDistance()
    {
        return lateralWheelDistance;
    }

    @Override
    public boolean update()
    {
        long read = stopwatch.read(TimeUnit.SECONDS);
        log.append(read).append(", ").append(leftTDS.getVelocity()).append(", ").append(rightTDS.getVelocity()).append("\n");
        toUpdate.update();
        return true;
    }
}
