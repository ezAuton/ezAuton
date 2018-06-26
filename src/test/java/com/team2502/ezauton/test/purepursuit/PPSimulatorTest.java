package com.team2502.ezauton.test.purepursuit;

import com.team2502.ezauton.actuators.IVelocityMotor;
import com.team2502.ezauton.command.PPCommand;
import com.team2502.ezauton.localization.TankRobotEncoderRotationEstimator;
import com.team2502.ezauton.pathplanning.PP_PathGenerator;
import com.team2502.ezauton.pathplanning.Path;
import com.team2502.ezauton.pathplanning.purepursuit.ILookahead;
import com.team2502.ezauton.pathplanning.purepursuit.LookaheadBounds;
import com.team2502.ezauton.pathplanning.purepursuit.PPWaypoint;
import com.team2502.ezauton.pathplanning.purepursuit.PurePursuitMovementStrategy;
import com.team2502.ezauton.robot.implemented.TankRobotTransLocDriveable;
import com.team2502.ezauton.test.simulator.SimulatedTankRobot;
import edu.wpi.first.wpilibj.command.Command;
import org.junit.Test;

public class PPSimulatorTest
{

    private static final double LATERAL_WHEEL_DIST = 4;
    private static final double WHEEL_SIZE = 0.5;

    @Test
    public void testSimulator()
    {

        PPWaypoint waypoint1 = PPWaypoint.simple2D(0, 0, 0, 3, -3);
        PPWaypoint waypoint2 = PPWaypoint.simple2D(0, 6, 5, 3, -3);
        PPWaypoint waypoint3 = PPWaypoint.simple2D(0, 12, 0, 3, -3);

        PP_PathGenerator pathGenerator = new PP_PathGenerator(waypoint1, waypoint2, waypoint3);
        Path path = pathGenerator.generate(0.05);

        PurePursuitMovementStrategy ppMoveStrat = new PurePursuitMovementStrategy(path, 1);

        SimulatedTankRobot robot = new SimulatedTankRobot(LATERAL_WHEEL_DIST,WHEEL_SIZE,0.05);

        IVelocityMotor leftMotor = robot.getLeftMotor();
        IVelocityMotor rightMotor = robot.getRightMotor();

        TankRobotEncoderRotationEstimator locEstimator = new TankRobotEncoderRotationEstimator(robot.getLeftWheel(), robot.getRightWheel(), robot);

        ILookahead lookahead = new LookaheadBounds(1, 5, 2, 10, locEstimator);

        TankRobotTransLocDriveable tankRobotTransLocDriveable = new TankRobotTransLocDriveable(leftMotor, rightMotor, locEstimator, locEstimator, robot);
        new PPCommand(ppMoveStrat, locEstimator, lookahead, tankRobotTransLocDriveable).test();
        System.out.println(locEstimator.estimateLocation());
    }
}
