package com.github.ezauton.core.test.purepursuit;

import com.github.ezauton.core.action.ActionGroup;
import com.github.ezauton.core.action.BackgroundAction;
import com.github.ezauton.core.action.PurePursuitAction;
import com.github.ezauton.core.actuators.IVelocityMotor;
import com.github.ezauton.core.helper.PathHelper;
import com.github.ezauton.core.localization.estimators.TankRobotEncoderEncoderEstimator;
import com.github.ezauton.core.pathplanning.PP_PathGenerator;
import com.github.ezauton.core.pathplanning.Path;
import com.github.ezauton.core.pathplanning.purepursuit.*;
import com.github.ezauton.core.robot.implemented.TankRobotTransLocDriveable;
import com.github.ezauton.core.simulation.SimulatedTankRobot;
import com.github.ezauton.core.simulation.TimeWarpedSimulation;
import com.github.ezauton.core.trajectory.geometry.ImmutableVector;
import com.github.ezauton.recorder.Recording;
import com.github.ezauton.recorder.base.PurePursuitRecorder;
import com.github.ezauton.recorder.base.RobotStateRecorder;
import com.github.ezauton.recorder.base.TankDriveableRecorder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PPSimulatorTest {

    private static final double LATERAL_WHEEL_DIST = 4;

//    @Test
    public void testLeftToRightScale() throws TimeoutException, ExecutionException {
        PPWaypoint[] build = new PPWaypoint.Builder()
                .add(0, 0, 16, 13, -12)
                .add(0, 4, 16, 13, -12)
                .add(-0.5, 8.589, 16, 13, -12)
                .add(-0.5, 12.405, 13, 13, -12)
                .add(-0.5, 17, 8.5, 13, -12)
                .add(1.5, 19.4, 0, 13, -12)
                .buildArray();

        test("testLeftToRightScale", build);
    }

    @Test
    public void testStraight() throws TimeoutException, ExecutionException {

        PPWaypoint waypoint1 = PPWaypoint.simple2D(0, 0, 0, 3, -4);
        PPWaypoint waypoint2 = PPWaypoint.simple2D(0, 6, 5, 3, -4);
        PPWaypoint waypoint3 = PPWaypoint.simple2D(0, 20, 0, 3, -4);

        test("testStraight", waypoint1, waypoint2, waypoint3);
    }

    @Test
    public void testStraightGeneric() throws TimeoutException, ExecutionException {
        test("testStraightGeneric", PathHelper.STRAIGHT_12UNITS);
    }

    @Test
    public void testRight() throws TimeoutException, ExecutionException {
        PPWaypoint waypoint1 = PPWaypoint.simple2D(0, 0, 0, 3, -3);
        PPWaypoint waypoint2 = PPWaypoint.simple2D(6, 6, 5, 3, -3);
        PPWaypoint waypoint3 = PPWaypoint.simple2D(12, 0, 0, 3, -3);

        test("testRight", waypoint1, waypoint2, waypoint3);
    }

    @Test
    public void testSpline() throws TimeoutException, ExecutionException {
        test("testSpline", new SplinePPWaypoint.Builder()
                .add(0, 0, 0, 15, 13, -12)
                .add(0, 13, 0, 10, 13, -12)
                .add(20, 17, -Math.PI / 2, 8, 13, -12)
                .add(23, 24, 0, 0.5, 13, -12)
                .buildPathGenerator()
                .generate(0.05));
    }

    private void test(String name, Path path) throws TimeoutException, ExecutionException {

        PurePursuitMovementStrategy ppMoveStrat = new PurePursuitMovementStrategy(path, 0.001);

        // Not a problem
        TimeWarpedSimulation simulation = new TimeWarpedSimulation(1);

        // Might be a problem
        SimulatedTankRobot simulatedRobot = new SimulatedTankRobot(LATERAL_WHEEL_DIST, simulation.getClock(), 14, 0.3, 16D);
        simulatedRobot.getDefaultLocEstimator().reset();
        IVelocityMotor leftMotor = simulatedRobot.getLeftMotor();
        IVelocityMotor rightMotor = simulatedRobot.getRightMotor();

        TankRobotEncoderEncoderEstimator locEstimator = new TankRobotEncoderEncoderEstimator(simulatedRobot.getLeftDistanceSensor(), simulatedRobot.getRightDistanceSensor(), simulatedRobot);
        locEstimator.reset();

        ILookahead lookahead = new LookaheadBounds(1, 5, 2, 10, locEstimator);

        TankRobotTransLocDriveable tankRobotTransLocDriveable = new TankRobotTransLocDriveable(leftMotor, rightMotor, locEstimator, locEstimator, simulatedRobot);

        Recording rec = new Recording();
        rec.addSubRecording(new PurePursuitRecorder(simulation.getClock(), path, ppMoveStrat));
        rec.addSubRecording(new RobotStateRecorder(simulation.getClock(), locEstimator, locEstimator, 30 / 12D, 2));
        rec.addSubRecording(new TankDriveableRecorder("td", simulation.getClock(), simulatedRobot.getDefaultTransLocDriveable()));


        PurePursuitAction purePursuitAction = new PurePursuitAction(20, TimeUnit.MILLISECONDS, ppMoveStrat, locEstimator, lookahead, tankRobotTransLocDriveable);

        BackgroundAction updateKinematics = new BackgroundAction(2, TimeUnit.MILLISECONDS, simulatedRobot::update);

        Recording recording = new Recording()
                .addSubRecording(new RobotStateRecorder("robotstate", simulation.getClock(), locEstimator, locEstimator, simulatedRobot.getLateralWheelDistance(), 1.5))
                .addSubRecording(new PurePursuitRecorder("pp", simulation.getClock(), path, ppMoveStrat))
                .addSubRecording(new TankDriveableRecorder("td", simulation.getClock(), tankRobotTransLocDriveable));

        BackgroundAction updateRecording = new BackgroundAction(20, TimeUnit.MILLISECONDS, recording::update);

        // Used to update the velocities of left and right motors while also updating the calculations for the location of the robot
        BackgroundAction backgroundAction = new BackgroundAction(20, TimeUnit.MILLISECONDS, locEstimator::update, rec::update);

        ActionGroup group = new ActionGroup()
                .with(updateKinematics)
                .with(backgroundAction)
                .with(updateRecording)
                .addSequential(purePursuitAction);
        simulation.add(group);

        // run the simulator for 30 seconds
        try {
            simulation.runSimulation(30, TimeUnit.SECONDS);
        } finally {
            try {
                recording.save(name + ".json");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        double leftWheelVelocity = locEstimator.getLeftTranslationalWheelVelocity();
        assertEquals(0, leftWheelVelocity, 0.5D, "left wheel velocity");

        double rightWheelVelocity = locEstimator.getRightTranslationalWheelVelocity();
        assertEquals(0, rightWheelVelocity, 0.5D, "right wheel velocity");

        // The final location after the simulator
        ImmutableVector finalLoc = locEstimator.estimateLocation();

        // If the final loc is approximately equal to the last waypoint
        approxEqual(path.getEnd(), finalLoc, 0.2);

        // If the final loc is approximately equal to the last waypoint
        approxEqual(path.getEnd(), finalLoc, 0.2);
    }

    /**
     * Test the path with a robot max acceleration 14ft/s^2, min velocity 0.3ft/s, maxVelocity 16ft/s
     *
     * @param waypoints
     */
    private void test(String name, PPWaypoint... waypoints) throws TimeoutException, ExecutionException {
        PP_PathGenerator pathGenerator = new PP_PathGenerator(waypoints);
        Path path = pathGenerator.generate(0.05);
        test(name, path);
    }

    private void approxEqual(ImmutableVector a, ImmutableVector b, double epsilon) {
        double[] bElements = b.getElements();
        double[] aElements = a.getElements();
        for (int i = 0; i < aElements.length; i++) {
            assertEquals(aElements[i], bElements[i], epsilon);
        }
    }
}
