package com.github.ezauton.core.pathplanning.ramsete;

import com.github.ezauton.core.action.ActionGroup;
import com.github.ezauton.core.action.BackgroundAction;
import com.github.ezauton.core.action.RamseteAction;
import com.github.ezauton.core.localization.TranslationalLocationEstimator;
import com.github.ezauton.core.localization.estimators.TankRobotEncoderEncoderEstimator;
import com.github.ezauton.core.pathplanning.LinearPathSegment;
import com.github.ezauton.core.pathplanning.Path;
import com.github.ezauton.core.pathplanning.purepursuit.PPWaypoint;
import com.github.ezauton.core.pathplanning.purepursuit.PurePursuitMovementStrategy;
import com.github.ezauton.core.pathplanning.purepursuit.SplinePPWaypoint;
import com.github.ezauton.core.robot.TankRobotConstants;
import com.github.ezauton.core.simulation.SimulatedTankRobot;
import com.github.ezauton.core.simulation.TimeWarpedSimulation;
import com.github.ezauton.core.trajectory.geometry.ImmutableVector;
import com.github.ezauton.core.utils.MathUtils;
import com.github.ezauton.recorder.Recording;
import com.github.ezauton.recorder.base.GenericNumberRecorder;
import com.github.ezauton.recorder.base.PurePursuitRecorder;
import com.github.ezauton.recorder.base.RobotStateRecorder;
import com.github.ezauton.recorder.base.TankDriveableRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.DoubleSupplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RamseteTest {
    private TimeStateSeries ramsete;

    @BeforeEach
    public void init() {
        ramsete = new TimeStateSeries(
                new SplinePPWaypoint.Builder()
                        .add(0, 0, 0, 10, 10, -10)
                        .add(10, 10, 0, 10, 10, -10)
                        .buildPathGenerator().generate(0.05), 0.01);
    }

    @Test
    public void testAngleFinder() {
        for (double i = -10; i < 10; i++) {
            if (i == 0) continue;

            LinearPathSegment segment = generatePathSegmentAtAngle(Math.PI / i);
            System.out.println("segment.getTo() = " + segment.getTo());
            assertEquals((Math.PI / i) % Math.PI, (ramsete.calculateThetaOfLinearPathSegment(segment)) % Math.PI, 1e-5);
        }
    }

    private LinearPathSegment generatePathSegmentAtAngle(double theta) {
        return (LinearPathSegment) new PPWaypoint.Builder().add(0, 0, 0, 0, 0)
                .add(-Math.sin(theta), Math.cos(theta), 0, 0, 0)
                .buildPathGenerator().generate(0.05).getPathSegments().get(0);
    }

    @Test
    public void testGeneratingGoalStates() {
        ramsete = new TimeStateSeries(
                new SplinePPWaypoint.Builder()
                        .add(0, 0, 0, 10, 10, -10)
                        .add(10, 10, 0, 10, 10, -10)
                        .buildPathGenerator().generate(0.05), 0.01);
        ramsete.updateInterpolationMaps(1e-3); //1 ms

        ramsete.printCSV();
    }

    @Test
    public void testBasicLinearPath() throws TimeoutException, ExecutionException {
        Path path = new PPWaypoint.Builder()
                .add(0, 0, 3, 10, -10)
                .add(0, 10, 0, 10, -10)
                .buildPathGenerator().generate(0.05);

        test("basicLinearPath", path, 2, 0.5);
    }

    @Test
    public void testBasicSplinePath() throws TimeoutException, ExecutionException {

            Path path = new SplinePPWaypoint.Builder()
                    .add(0, 0, 0, 3, 10, -10)
                    .add(0, 10, -Math.PI / 2, 3, 10, -10)
                    .add(-7, 3, Math.PI / 2, 0, 10, -10)
                    .buildPathGenerator().generate(0.05);
            test("basicSplinePath", path, 0.3, 0);
    }

    @Test
    public void testMoreComplexSplinePath() throws TimeoutException, ExecutionException {

        Path path = new SplinePPWaypoint.Builder()
                .add(0, 0, 0, 10, 3, 10, -10)
                .add(0, 10, 0, 10, 3, 10, -10)
                .add(10, 10,    0, 10, 3, 10, -10)
                .add(10, 20,    0, 10, 0, 10, -10)
                .buildPathGenerator().generate(0.05);
        test("complexSplinePath", path, 0.25, 0.05);
    }

    @Test
    public void testRightCurlingPath() throws TimeoutException, ExecutionException {

        Path path = new SplinePPWaypoint.Builder()
                .add(0, 0, 0, 10, 3, 10, -10)
                .add(-2, 7, -8, 8, 3, 10, -10)
                .add(-10, 5, -10, -10, 3, 10, -10)
                .add(-10, 0, 8, 0, 3, 10, -10)
                .add(0, 0, 10, 0, 0, 10, -10)
                .flipY().buildPathGenerator().generate(0.05);
        test("testRightCurlingPath", path, 0.1, 3);
    }

    @Test
    public void testLeftCurlingPath() throws TimeoutException, ExecutionException {

        Path path = new SplinePPWaypoint.Builder()
                .add(0, 0, 0, 10, 3, 10, -10)
                .add(-2, 7, -8, 8, 3, 10, -10)
                .add(-10, 5, -10, -10, 3, 10, -10)
                .add(-10, 0, 8, 0, 3, 10, -10)
                .add(0, 0,    10, 0, 0, 10, -10)
                .buildPathGenerator().generate(0.05);
        test("testLeftCurlingPath", path, 0.1, 3);
    }




    private void test(String name, Path path, double b, double zeta) throws TimeoutException, ExecutionException {
        System.out.println("b = " + b);
        System.out.println("zeta = " + zeta);
        TimeWarpedSimulation sim = new TimeWarpedSimulation(3);

        SimulatedTankRobot robot = new SimulatedTankRobot(1, sim.getClock(), 35, 0, 15  );
        TankRobotConstants tankRobotConstants = robot.getDefaultTransLocDriveable().getTankRobotConstants();
        RamseteMovementStrategy ramseteMovementStrategy = new RamseteMovementStrategy(b, zeta, 0.25, tankRobotConstants, path, 0.05);

        PurePursuitMovementStrategy ppms = new PurePursuitMovementStrategy(path, 0.1);

        TankRobotEncoderEncoderEstimator locEstimator = robot.getDefaultLocEstimator();
        locEstimator.reset();

        TankRobotEncoderEncoderEstimator rotEstimator = robot.getDefaultLocEstimator();
        RamseteAction ramseteAction = new RamseteAction(
                1,
                TimeUnit.MILLISECONDS,
                ramseteMovementStrategy,
                locEstimator,
                rotEstimator,
                robot.getDefaultTransLocDriveable()
        );

        HashMap<String, DoubleSupplier> numberProducers = new HashMap<>();
        for (String key : ramseteMovementStrategy.getRamseteFrame().keySet()) {
            numberProducers.put(key + "  ", () -> ramseteMovementStrategy.getRamseteFrame().get(key));
        }

        GenericNumberRecorder gnrec = new GenericNumberRecorder("ramsete", sim.getClock(), numberProducers);
        Recording rec = new Recording()
                .addSubRecording(new RobotStateRecorder("robotstate", sim.getClock(), locEstimator, rotEstimator, tankRobotConstants.getLateralWheelDistance(), 1.5))
                .addSubRecording(gnrec)
                .addSubRecording(new RobotStateRecorder("referenceState", sim.getClock(),
                        new TranslationalLocationEstimator() {
                            @Override
                            public ImmutableVector estimateLocation() {
                                RamseteMovementStrategy.Pose desiredPose = ramseteMovementStrategy.getRamsetePath().getDesiredPose(ramseteAction.getStopwatch().read() / 1000D).getPose();
                                return new ImmutableVector(desiredPose.getX(), desiredPose.getY());
                            }

                            @Override
                            public ImmutableVector estimateAbsoluteVelocity() {
                                RamseteMovementStrategy.ControlOutput controlOutput = ramseteMovementStrategy.getRamsetePath().getDesiredPose(ramseteAction.getStopwatch().read() / 1000D).getDesiredOutput();
                                RamseteMovementStrategy.Pose pose = ramseteMovementStrategy.getRamsetePath().getDesiredPose(ramseteAction.getStopwatch().read() / 1000D).getPose();
                                return MathUtils.Geometry.getVector(controlOutput.getVelocity(), pose.getTheta());
                            }
                        },
                        () -> {
                            RamseteMovementStrategy.Pose desiredPose = ramseteMovementStrategy.getRamsetePath().getDesiredPose(ramseteAction.getStopwatch().read() / 1000D).getPose();
                            return desiredPose.getTheta();
                        }, 0.01, 0.01))
                .addSubRecording(new PurePursuitRecorder(sim.getClock(), path, ppms))
                .addSubRecording(new TankDriveableRecorder("tankrobot", sim.getClock(), robot.getDefaultTransLocDriveable()));

        BackgroundAction updateKinematics = new BackgroundAction(2, TimeUnit.MILLISECONDS, robot::update);
        BackgroundAction updateRecording = new BackgroundAction(10, TimeUnit.MILLISECONDS, rec::update);

        ActionGroup group = new ActionGroup()
                .with(updateKinematics).with(updateRecording)
                .addSequential(ramseteAction);

        sim.add(group);
        // run the simulator for 30 seconds
        try {
            sim.runSimulation(3, TimeUnit.SECONDS);
        } finally {
            try {
                rec.save(name + ".json");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //TODO: Add some juicy asserts
    }

}
