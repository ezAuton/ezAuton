package com.github.ezauton.wpilibexample;

import org.github.ezauton.ezauton.actuators.IVelocityMotor;
import org.github.ezauton.ezauton.actuators.IVoltageMotor;
import org.github.ezauton.ezauton.localization.sensors.ITranslationalDistanceSensor;
import org.github.ezauton.ezauton.robot.ITankRobotConstants;
import org.github.ezauton.ezauton.utils.IClock;

public class RobotData {
    private final IVelocityMotor leftMotorVel;
    private final IVelocityMotor rightMotorVel;
    private final IVoltageMotor leftMotorVolt;
    private final IVoltageMotor rightMotorVolt;
    private final ITranslationalDistanceSensor distanceSensorLeft;
    private final ITranslationalDistanceSensor distanceSensorRight;
    private final ITankRobotConstants robotConstants;
    private final IClock clock;
    private final double length;

    public RobotData(
            IVelocityMotor leftMotorVel,
            IVelocityMotor rightMotorVel,
            IVoltageMotor leftMotorVolt,
            IVoltageMotor rightMotorVolt,
            ITranslationalDistanceSensor distanceSensorLeft,
            ITranslationalDistanceSensor distanceSensorRight,
            ITankRobotConstants robotConstants,
            IClock clock,
            double length
    ) {
        this.leftMotorVel = leftMotorVel;
        this.rightMotorVel = rightMotorVel;
        this.leftMotorVolt = leftMotorVolt;
        this.rightMotorVolt = rightMotorVolt;
        this.distanceSensorLeft = distanceSensorLeft;
        this.distanceSensorRight = distanceSensorRight;
        this.robotConstants = robotConstants;
        this.clock = clock;
        this.length = length;
    }

    public IVelocityMotor getLeftMotorVel()
    {
        return leftMotorVel;
    }

    public IVelocityMotor getRightMotorVel()
    {
        return rightMotorVel;
    }

    public IVoltageMotor getLeftMotorVolt()
    {
        return leftMotorVolt;
    }

    public IVoltageMotor getRightMotorVolt()
    {
        return rightMotorVolt;
    }

    public ITranslationalDistanceSensor getDistanceSensorLeft()
    {
        return distanceSensorLeft;
    }

    public ITranslationalDistanceSensor getDistanceSensorRight()
    {
        return distanceSensorRight;
    }

    public ITankRobotConstants getRobotConstants()
    {
        return robotConstants;
    }

    public IClock getClock()
    {
        return clock;
    }

    public double getLength()
    {
        return length;
    }
}
