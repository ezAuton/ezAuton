package com.team2502.ezauton.helper;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.team2502.ezauton.actuators.Actuators;
import com.team2502.ezauton.actuators.IVoltageMotor;
import com.team2502.ezauton.actuators.RampUpSimulatedMotor;
import com.team2502.ezauton.command.PPCommand;
import com.team2502.ezauton.localization.estimators.TankRobotEncoderEncoderEstimator;
import com.team2502.ezauton.pathplanning.Path;
import com.team2502.ezauton.pathplanning.purepursuit.ILookahead;
import com.team2502.ezauton.pathplanning.purepursuit.LookaheadBounds;
import com.team2502.ezauton.pathplanning.purepursuit.PurePursuitMovementStrategy;
import com.team2502.ezauton.robot.ITankRobotConstants;
import com.team2502.ezauton.robot.implemented.TankRobotTransLocDriveable;
import com.team2502.ezauton.utils.InterpolationMap;
import com.team2502.ezauton.utils.OddInterpolationMap;
import com.team2502.ezauton.utils.RealStopwatch;
import edu.wpi.first.wpilibj.command.Command;

public class EzVoltagePPBuilder
{

    private BaseMotorController leftMotor;
    private BaseMotorController rightMotor;
    private double lateralWheelDist;
    private InterpolationMap interpolationMap = new OddInterpolationMap(0D,0D);
    private ILookahead lookahead = null;

    public EzVoltagePPBuilder()
    {
    }

    public EzVoltagePPBuilder addSpeedPair(double speed, double voltage)
    {
        interpolationMap.put(speed, voltage);
        return this;
    }

    public EzVoltagePPBuilder addLeft(BaseMotorController leftMotor)
    {
        this.leftMotor = leftMotor;
        return this;
    }

    public EzVoltagePPBuilder addRight(BaseMotorController rightMotor)
    {
        this.rightMotor = rightMotor;
        return this;
    }

    public EzVoltagePPBuilder addLateralWheelDist(double lateralWheelDist)
    {
        this.lateralWheelDist = lateralWheelDist;
        return this;
    }


    public Command build(Path path, double dvMax)
    {
        if(interpolationMap.size() == 1)
        {
            throw new IllegalArgumentException("Must add at least one pair!");
        }
        if(leftMotor == null || rightMotor == null)
        {
            throw new IllegalArgumentException("Both left and right motors must have been initialized!");
        }

        PurePursuitMovementStrategy ppMoveStrat = new PurePursuitMovementStrategy(path, 0.1D);
        RampUpSimulatedMotor left = new RampUpSimulatedMotor(new RealStopwatch(), dvMax);
        RampUpSimulatedMotor right = new RampUpSimulatedMotor(new RealStopwatch(), dvMax);

        IVoltageMotor leftVolt = volt -> leftMotor.set(ControlMode.PercentOutput, volt);
        left.setSubscribed(Actuators.roughConvertVoltageToVel(leftVolt, interpolationMap));

        IVoltageMotor rightVolt = volt -> rightMotor.set(ControlMode.PercentOutput, volt);
        right.setSubscribed(Actuators.roughConvertVoltageToVel(rightVolt, interpolationMap));

        ITankRobotConstants constants = () -> lateralWheelDist;

        TankRobotEncoderEncoderEstimator locEstimator = new TankRobotEncoderEncoderEstimator(left, right, constants);

        if(this.lookahead == null)
        {
            lookahead = new LookaheadBounds(1, 5, 2, 10, locEstimator);
        }

        TankRobotTransLocDriveable tankRobotTransLocDriveable = new TankRobotTransLocDriveable(left, right, locEstimator, locEstimator, constants);
        return new PPCommand(ppMoveStrat, locEstimator, lookahead, tankRobotTransLocDriveable, locEstimator).buildWPI();
    }
}
