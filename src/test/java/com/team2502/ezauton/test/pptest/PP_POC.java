package com.team2502.ezauton.test.pptest;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team2502.ezauton.actuators.IVelocityMotor;
import com.team2502.ezauton.command.PPCommand;
import com.team2502.ezauton.localization.ITranslationalLocationEstimator;
import com.team2502.ezauton.localization.TankRobotEncoderRotationEstimator;
import com.team2502.ezauton.localization.sensors.EncoderWheel;
import com.team2502.ezauton.localization.sensors.Encoders;
import com.team2502.ezauton.localization.sensors.IEncoder;
import com.team2502.ezauton.pathplanning.PP_PathGenerator;
import com.team2502.ezauton.pathplanning.Path;
import com.team2502.ezauton.pathplanning.purepursuit.ILookahead;
import com.team2502.ezauton.pathplanning.purepursuit.LookaheadBounds;
import com.team2502.ezauton.pathplanning.purepursuit.PPWaypoint;
import com.team2502.ezauton.pathplanning.purepursuit.PurePursuitMovementStrategy;
import com.team2502.ezauton.robot.ITankRobotConstants;
import com.team2502.ezauton.robot.implemented.TankRobotTransLocDriveable;

public class PP_POC
{

    public void exampleCommand()
    {

        TalonSRX leftTalon = new TalonSRX(1);
        TalonSRX rightTalon = new TalonSRX(2);

        PPWaypoint waypoint1 = PPWaypoint.simple2D(0, 0, 0, 3, -3);
        PPWaypoint waypoint2 = PPWaypoint.simple2D(0, 6, 5, 3, -3);
        PPWaypoint waypoint3 = PPWaypoint.simple2D(0, 12, 0, 3, -3);

        PP_PathGenerator pathGenerator = new PP_PathGenerator(waypoint1,waypoint2,waypoint3);
        Path path = pathGenerator.generate(0.05);

        PurePursuitMovementStrategy ppMoveStrat = new PurePursuitMovementStrategy(path,0.1);

        IVelocityMotor leftMotor = velocity -> leftTalon.set(ControlMode.Velocity, velocity);
        IVelocityMotor rightMotor = velocity -> rightTalon.set(ControlMode.Velocity, velocity);

        IEncoder leftEncoder = Encoders.fromTalon(leftTalon, Encoders.CTRE_MAG_ENCODER);
        EncoderWheel leftEncoderWheel = new EncoderWheel(leftEncoder,3);

        IEncoder rightEncoder = Encoders.fromTalon(rightTalon, Encoders.CTRE_MAG_ENCODER);
        EncoderWheel rightEncoderWheel = new EncoderWheel(rightEncoder,3);

        ITankRobotConstants constants = () -> 20;

        TankRobotEncoderRotationEstimator locEstimator = new TankRobotEncoderRotationEstimator(leftEncoderWheel,rightEncoderWheel, constants);

        ILookahead lookahead = new LookaheadBounds(1,5,2,10,locEstimator);

        TankRobotTransLocDriveable tankRobotTransLocDriveable = new TankRobotTransLocDriveable(leftMotor, rightMotor, locEstimator, locEstimator, constants);
        PPCommand ppCommand = new PPCommand(ppMoveStrat, locEstimator, lookahead, tankRobotTransLocDriveable);
    }
}
