package com.team2502.ezauton.test.simulator;

import com.team2502.ezauton.localization.sensors.EncoderWheel;
import com.team2502.ezauton.localization.sensors.Encoders;
import com.team2502.ezauton.localization.sensors.ITachometer;
import com.team2502.ezauton.robot.ITankRobotConstants;
import com.team2502.ezauton.utils.SimulatedStopwatch;

public class SimulatedTankRobot implements ITankRobotConstants
{

    public static final double NORM_DT = 0.02D;

    public static final float MAX_VEL = 16F;
    public static final float VOLTAGE_CHANGE_MAX = .02F;
    public static final float LATERAL_WHEEL_DIST = 2F;

    private final double lateralWheelDistance;

    private final EncoderWheel left;
    private final EncoderWheel right;
    private final double dt;
    private final double wheelSize;

    private float leftMotorPercentVoltage = 0;
    private float rightMotorPercentVoltage = 0;

    public SimulatedTankRobot(double lateralWheelDistance, double wheelSize, double dt)
    {
        this.dt = dt;
        this.wheelSize = wheelSize;

        this.lateralWheelDistance = lateralWheelDistance;

        ITachometer leftTach = this::getLeftVel;

        ITachometer rightTach = this::getLeftVel;

        SimulatedStopwatch stopwatch = new SimulatedStopwatch(dt);

        left = new EncoderWheel(Encoders.fromTachometer(leftTach, stopwatch.clone()), wheelSize);
        right = new EncoderWheel(Encoders.fromTachometer(rightTach, stopwatch.clone()), wheelSize);
    }

    public EncoderWheel getLeft()
    {
        return left;
    }

    public EncoderWheel getRight()
    {
        return right;
    }

    public double getLateralWheelDistance()
    {
        return lateralWheelDistance;
    }

    /**
     * Run motors at a certain velocity
     *
     * @param leftVel
     * @param rightVel
     */
    public void runMotorsVel(float leftVel, float rightVel)
    {
        leftMotorPercentVoltage = runMotorVel(leftVel, leftMotorPercentVoltage);
        rightMotorPercentVoltage = runMotorVel(rightVel, rightMotorPercentVoltage);
    }

    /**
     * @return Get velocity given current status
     */
    public float getLeftVel()
    {
        return leftMotorPercentVoltage * MAX_VEL; //TODO
    }

    /**
     * @return Get velocity given current status
     */
    public float getRightVel()
    {
        return rightMotorPercentVoltage * MAX_VEL; //TODO
    }

    private float runMotorVel(float velocity, float currentVoltage)
    {
        float percentVoltage = velocity / MAX_VEL;
        if(percentVoltage > 1)
        {
            percentVoltage = 1;
        }
        else if(percentVoltage < -1)
        {
            percentVoltage = -1;
        }
        return runMotorVoltage(percentVoltage, currentVoltage);
    }

    private float runMotorVoltage(float percentVoltage, float currentVoltage)
    {
        float dif = percentVoltage - currentVoltage;
        if(dif < -VOLTAGE_CHANGE_MAX)
        {
            percentVoltage = currentVoltage - VOLTAGE_CHANGE_MAX;
        }
        else if(dif > VOLTAGE_CHANGE_MAX)
        {
            percentVoltage = currentVoltage + VOLTAGE_CHANGE_MAX;
        }
        return percentVoltage;
    }
}
