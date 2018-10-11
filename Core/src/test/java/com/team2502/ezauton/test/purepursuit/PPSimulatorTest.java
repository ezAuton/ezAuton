package com.team2502.ezauton.test.purepursuit;

import com.team2502.ezauton.actuators.IVelocityMotor;
import com.team2502.ezauton.command.*;
import com.team2502.ezauton.localization.estimators.TankRobotEncoderEncoderEstimator;
import com.team2502.ezauton.pathplanning.PP_PathGenerator;
import com.team2502.ezauton.pathplanning.Path;
import com.team2502.ezauton.pathplanning.purepursuit.ILookahead;
import com.team2502.ezauton.pathplanning.purepursuit.LookaheadBounds;
import com.team2502.ezauton.pathplanning.purepursuit.PPWaypoint;
import com.team2502.ezauton.pathplanning.purepursuit.PurePursuitMovementStrategy;
import com.team2502.ezauton.robot.implemented.TankRobotTransLocDriveable;
import com.team2502.ezauton.test.simulator.SimulatedTankRobot;
import com.team2502.ezauton.trajectory.geometry.ImmutableVector;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class PPSimulatorTest
{

    private static final double LATERAL_WHEEL_DIST = 4;

    @Test
    public void testStraight()
    {

        PPWaypoint waypoint1 = PPWaypoint.simple2D(0, 0, 0, 3, -4);
        PPWaypoint waypoint2 = PPWaypoint.simple2D(0, 6, 5, 3, -4);
        PPWaypoint waypoint3 = PPWaypoint.simple2D(0, 20, 0, 3, -4);

        test(waypoint1, waypoint2, waypoint3);
    }

    @Test
    public void testRight()
    {
        PPWaypoint waypoint1 = PPWaypoint.simple2D(0, 0, 0, 3, -3);
        PPWaypoint waypoint2 = PPWaypoint.simple2D(6, 6, 5, 3, -3);
        PPWaypoint waypoint3 = PPWaypoint.simple2D(12, 0, 0, 3, -3);

        test(waypoint1, waypoint2, waypoint3);
    }

    private void test(PPWaypoint... waypoints)
    {
        PP_PathGenerator pathGenerator = new PP_PathGenerator(waypoints);
        Path path = pathGenerator.generate(0.05);

        PurePursuitMovementStrategy ppMoveStrat = new PurePursuitMovementStrategy(path, 0.001);

//        ICopyable stopwatch = Simulation.getInstance().generateStopwatch();
        Simulation simulation = new Simulation();

        SimulatedTankRobot robot = new SimulatedTankRobot(LATERAL_WHEEL_DIST, simulation.getClock(), 14, 0.3, 16D);

        IVelocityMotor leftMotor = robot.getLeftMotor();
        IVelocityMotor rightMotor = robot.getRightMotor();

        TankRobotEncoderEncoderEstimator locEstimator = new TankRobotEncoderEncoderEstimator(robot.getLeftDistanceSensor(), robot.getRightDistanceSensor(), robot);
        locEstimator.reset();

        // Used to update the velocities of left and right motors while also updating the calculations for the location of the robot
        BackgroundAction backgroundAction = new BackgroundAction(TimeUnit.MILLISECONDS, 20, locEstimator, robot);

        simulation.add(backgroundAction);

        ILookahead lookahead = new LookaheadBounds(1, 5, 2, 10, locEstimator);

        TankRobotTransLocDriveable tankRobotTransLocDriveable = new TankRobotTransLocDriveable(leftMotor, rightMotor, locEstimator, locEstimator, robot);

        PPCommand ppCommand = new PPCommand(TimeUnit.MILLISECONDS, 50, ppMoveStrat, locEstimator, lookahead, tankRobotTransLocDriveable);

        // Run the ppCommand and then kill the background task as it is no longer needed
        ActionGroup actionGroup = new ActionGroup(ppCommand, new BaseAction(backgroundAction::end));

        simulation.add(actionGroup);

        // run the simulator with a timeout of 100 seconds
        simulation.run(TimeUnit.SECONDS, 5);

        double leftWheelVelocity = locEstimator.getLeftTranslationalWheelVelocity();
        Assert.assertEquals(0, leftWheelVelocity, 0.2D);

        double rightWheelVelocity = locEstimator.getRightTranslationalWheelVelocity();
        Assert.assertEquals(0, rightWheelVelocity, 0.2D);

        // The final location after the simulator
        ImmutableVector finalLoc = locEstimator.estimateLocation();

        // If the final loc is approximately equal to the last waypoint
        approxEqual(waypoints[waypoints.length - 1].getLocation(), finalLoc, 0.2);
    }

    private void approxEqual(ImmutableVector a, ImmutableVector b, double epsilon)
    {
        double[] bElements = b.getElements();
        double[] aElements = a.getElements();
        for(int i = 0; i < aElements.length; i++)
        {
            Assert.assertEquals(aElements[i], bElements[i], epsilon);
        }
    }
}
