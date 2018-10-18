package org.github.ezauton.ezauton.test.purepursuit;

import org.github.ezauton.ezauton.action.*;
import org.github.ezauton.ezauton.action.simulation.MultiThreadSimulation;
import org.github.ezauton.ezauton.actuators.IVelocityMotor;
import org.github.ezauton.ezauton.localization.estimators.TankRobotEncoderEncoderEstimator;
import org.github.ezauton.ezauton.pathplanning.PP_PathGenerator;
import org.github.ezauton.ezauton.pathplanning.Path;
import org.github.ezauton.ezauton.pathplanning.purepursuit.ILookahead;
import org.github.ezauton.ezauton.pathplanning.purepursuit.LookaheadBounds;
import org.github.ezauton.ezauton.pathplanning.purepursuit.PPWaypoint;
import org.github.ezauton.ezauton.pathplanning.purepursuit.PurePursuitMovementStrategy;
import org.github.ezauton.ezauton.robot.implemented.TankRobotTransLocDriveable;
import org.github.ezauton.ezauton.test.simulator.SimulatedTankRobot;
import org.github.ezauton.ezauton.trajectory.geometry.ImmutableVector;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        // Not a problem
        MultiThreadSimulation simulation = new MultiThreadSimulation(1);

        // Might be a problem
        SimulatedTankRobot robot = new SimulatedTankRobot(LATERAL_WHEEL_DIST, simulation.getClock(), 14, 0.3, 16D);

        IVelocityMotor leftMotor = robot.getLeftMotor();
        IVelocityMotor rightMotor = robot.getRightMotor();

        TankRobotEncoderEncoderEstimator locEstimator = new TankRobotEncoderEncoderEstimator(robot.getLeftDistanceSensor(), robot.getRightDistanceSensor(), robot);
        locEstimator.reset();

        ILookahead lookahead = new LookaheadBounds(1, 5, 2, 10, locEstimator);

        TankRobotTransLocDriveable tankRobotTransLocDriveable = new TankRobotTransLocDriveable(leftMotor, rightMotor, locEstimator, locEstimator, robot);

        PPCommand ppCommand = new PPCommand(20, TimeUnit.MILLISECONDS, ppMoveStrat, locEstimator, lookahead, tankRobotTransLocDriveable);

        BackgroundAction updateKinematics = new BackgroundAction(2, TimeUnit.MILLISECONDS, robot);
        // Used to update the velocities of left and right motors while also updating the calculations for the location of the robot
        BackgroundAction backgroundAction = new BackgroundAction(20, TimeUnit.MILLISECONDS, locEstimator);

        // Run the ppCommand and then kill the background task as it is no longer needed
        ActionGroup actionGroup = ActionGroup.ofSequentials(ppCommand, new BaseAction(backgroundAction::end));

        simulation
                .add(updateKinematics)
                .add(backgroundAction)
                .add(actionGroup);

        // run the simulator with a timeout of 20 seconds
        simulation.run(10, TimeUnit.SECONDS);

        // test
        String homeDir = System.getProperty("user.home");
        java.nio.file.Path filePath = Paths.get(homeDir, ".ezauton", "log.txt");

        try
        {
            Files.createDirectories(filePath.getParent());
            BufferedWriter writer = Files.newBufferedWriter(filePath);
            writer.write(robot.log.toString());

            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        double leftWheelVelocity = locEstimator.getLeftTranslationalWheelVelocity();
        Assert.assertEquals("left wheel velocity", 0, leftWheelVelocity, 0.5D);

        double rightWheelVelocity = locEstimator.getRightTranslationalWheelVelocity();
        Assert.assertEquals("right wheel velocity", 0, rightWheelVelocity, 0.5D);

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
            Assert.assertEquals("vector["+i+"]",aElements[i], bElements[i], epsilon);
        }
    }
}
