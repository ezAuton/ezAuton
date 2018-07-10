package com.team2502.ezauton.test.purepursuit;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team2502.ezauton.actuators.IVelocityMotor;
import com.team2502.ezauton.actuators.implementations.BaseSimulatedMotor;
import com.team2502.ezauton.actuators.implementations.BoundedVelocityProcessor;
import com.team2502.ezauton.actuators.implementations.RampUpVelocityProcessor;
import com.team2502.ezauton.command.BackgroundAction;
import com.team2502.ezauton.command.PPCommand;
import com.team2502.ezauton.helper.Paths;
import com.team2502.ezauton.localization.estimators.TankRobotEncoderEncoderEstimator;
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
import com.team2502.ezauton.utils.RealStopwatch;
import edu.wpi.first.wpilibj.command.Command;

public class PPExamples
{


    /**
     * Uses encoders
     * Minimal use of helper methods
     */
    public void exampleNoHelpers()
    {

        TalonSRX leftTalon = new TalonSRX(1);
        TalonSRX rightTalon = new TalonSRX(2);

        // (x, y, speed, acceleration, deceleration)
        PPWaypoint waypoint1 = PPWaypoint.simple2D(0, 0, 0, 3, -3);
        PPWaypoint waypoint2 = PPWaypoint.simple2D(0, 6, 5, 3, -3);
        PPWaypoint waypoint3 = PPWaypoint.simple2D(0, 12, 0, 3, -3);

        PP_PathGenerator pathGenerator = new PP_PathGenerator(waypoint1, waypoint2, waypoint3);

        // Generates a path by looking for nominal poses every 0.05 (dt) seconds. A small dt will yield more precision. The path will automatically interpolate between generated poses.
        Path path = pathGenerator.generate(0.05);

        // The strategy for moving. The stop tolerance is the distance away from the endpoint where Pure Pursuit is happy.
        PurePursuitMovementStrategy ppMoveStrat = new PurePursuitMovementStrategy(path, 0.1D);

        // Means to easily interface with motors
        IVelocityMotor leftMotor = velocity -> leftTalon.set(ControlMode.Velocity, velocity * Encoders.CTRE_MAG_ENCODER);
        IVelocityMotor rightMotor = velocity -> rightTalon.set(ControlMode.Velocity, velocity * Encoders.CTRE_MAG_ENCODER);

        // Means to easily interface with encoders
        IEncoder leftEncoder = Encoders.fromTalon(leftTalon, Encoders.CTRE_MAG_ENCODER);
        EncoderWheel leftEncoderWheel = new EncoderWheel(leftEncoder, 3);

        IEncoder rightEncoder = Encoders.fromTalon(rightTalon, Encoders.CTRE_MAG_ENCODER);
        EncoderWheel rightEncoderWheel = new EncoderWheel(rightEncoder, 3);

        // The lateral wheel distance between wheels
        ITankRobotConstants constants = () -> 20;

        // Encoder-encoder location estimator
        TankRobotEncoderEncoderEstimator locEstimator = new TankRobotEncoderEncoderEstimator(leftEncoderWheel, rightEncoderWheel, constants);

        // Dynamic lookahead with speed (speed comes from location estimator)
        ILookahead lookahead = new LookaheadBounds(1, 5, 2, 10, locEstimator);

        // An implementation for the robot to move toward a point at a provided speed
        TankRobotTransLocDriveable tankRobotTransLocDriveable = new TankRobotTransLocDriveable(leftMotor, rightMotor, locEstimator, locEstimator, constants);

        // Background task to periodically update location calculations
        Thread thread = new BackgroundAction(locEstimator).buildThread(10);
        thread.start();

        // Command to start Pure Pursuit
        Command commmand = new PPCommand(ppMoveStrat, locEstimator, lookahead, tankRobotTransLocDriveable).buildWPI();
    }

    public void exampleVoltage()
    {
        TalonSRX leftTalon = new TalonSRX(1);
        TalonSRX rightTalon = new TalonSRX(2);

        PurePursuitMovementStrategy ppMoveStrat = new PurePursuitMovementStrategy(Paths.STRAIGHT_12FT, 0.1D);

        // max speed of robot in feet. This can be any unit; however, units must be consistent across entire use of PP.
        double maxRobotSpeed = 16;

        // We need to limit acceleration for voltage drive because the motor will always need to run within its bounds to
        // get accurate localization
        double maxAccelPerSecond = 3D;

        // These RampUpSimulatedMotors provide a ramp up when setting a voltage. For example, if you immediately want 100% voltage the motor will actually slowly be set
        // From 0% to 100%. This smooth transition between voltage allows for easier localization as the relationship between voltage and velocity is predictable (and linear for most FRC motors)
        BaseSimulatedMotor leftMotorBase = new BaseSimulatedMotor(new RealStopwatch());
        RampUpVelocityProcessor leftRampUpMotor = new RampUpVelocityProcessor(leftMotorBase, new RealStopwatch(), maxAccelPerSecond);
        BoundedVelocityProcessor leftMotor = new BoundedVelocityProcessor(leftRampUpMotor,maxRobotSpeed);

        BaseSimulatedMotor rightMotorBase = new BaseSimulatedMotor(new RealStopwatch());
        RampUpVelocityProcessor rightRampUpMotor = new RampUpVelocityProcessor(rightMotorBase, new RealStopwatch(), maxAccelPerSecond);
        BoundedVelocityProcessor rightMotor = new BoundedVelocityProcessor(rightRampUpMotor,maxRobotSpeed);

        ITankRobotConstants constants = () -> 5;

        TankRobotEncoderEncoderEstimator locEstimator = new TankRobotEncoderEncoderEstimator(leftMotorBase, rightMotorBase, constants);

        ILookahead lookahead = new LookaheadBounds(1, 5, 2, 10, locEstimator);

        TankRobotTransLocDriveable tankRobotTransLocDriveable = new TankRobotTransLocDriveable(leftMotor, rightMotor, locEstimator, locEstimator, constants);

        // Background task to update location and percent voltage applied to motors. Will run every 10ms.
        Thread thread = new BackgroundAction(locEstimator, leftRampUpMotor, rightRampUpMotor).buildThread(10);
        thread.start();

        // Command to start Pure Pursuit
        Command commmand = new PPCommand(ppMoveStrat, locEstimator, lookahead, tankRobotTransLocDriveable).buildWPI();
    }

}
