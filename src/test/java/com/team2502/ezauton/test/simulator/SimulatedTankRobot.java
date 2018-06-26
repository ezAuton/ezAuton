package com.team2502.ezauton.test.simulator;

import com.team2502.ezauton.actuators.IVelocityMotor;
import com.team2502.ezauton.localization.sensors.EncoderWheel;
import com.team2502.ezauton.localization.sensors.Encoders;
import com.team2502.ezauton.localization.sensors.ITachometer;
import com.team2502.ezauton.robot.ITankRobotConstants;
import com.team2502.ezauton.utils.SimulatedStopwatch;

public class SimulatedTankRobot implements ITankRobotConstants
{

    public static final double NORM_DT = 0.02D;

    public static final double MAX_VEL = 16F;
    public static final double VOLTAGE_CHANGE_MAX = .02F;
    public static final double LATERAL_WHEEL_DIST = 2F;

    private final double lateralWheelDistance;

    private final EncoderWheel left;
    private final EncoderWheel right;
    private final double dt;
    private final double wheelSize;
    
    private final IVelocityMotor leftMotor = this::runLeftMotorVel;
    private final IVelocityMotor rightMotor = this::runRightMotorVel;

    private double leftMotorPercentVoltage = 0;
    private double rightMotorPercentVoltage = 0;

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

    public IVelocityMotor getLeftMotor()
    {
        return leftMotor;
    }

    public IVelocityMotor getRightMotor()
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

    /**
     * Run motors at a certain velocity
     *
     * @param leftVel
     * @param rightVel
     */
    public void runMotorsVel(double leftVel, double rightVel)
    {
        runLeftMotorVel(leftVel);
        runRightMotorVel(rightVel);
    }
    
    public void runLeftMotorVel(double leftVel)
    {
        leftMotorPercentVoltage = runMotorVel(leftVel, leftMotorPercentVoltage);
    }

    public void runRightMotorVel(double rightVel)
    {
        rightMotorPercentVoltage = runMotorVel(rightVel, rightMotorPercentVoltage);
    }

    /**
     * @return Get velocity given current status
     */
    public double getLeftVel()
    {
        return leftMotorPercentVoltage * MAX_VEL; //TODO
    }

    /**
     * @return Get velocity given current status
     */
    public double getRightVel()
    {
        return rightMotorPercentVoltage * MAX_VEL; //TODO
    }

    private double runMotorVel(double velocity, double currentVoltage)
    {
        double percentVoltage = velocity / MAX_VEL;
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

    private double runMotorVoltage(double percentVoltage, double currentVoltage)
    {
        double dif = percentVoltage - currentVoltage;
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
