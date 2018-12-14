package org.github.ezauton.ezauton.recorder;

import org.github.ezauton.ezauton.action.ActionGroup;
import org.github.ezauton.ezauton.action.BackgroundAction;
import org.github.ezauton.ezauton.action.PPCommand;
import org.github.ezauton.ezauton.action.simulation.MultiThreadSimulation;
import org.github.ezauton.ezauton.localization.estimators.TankRobotEncoderEncoderEstimator;
import org.github.ezauton.ezauton.pathplanning.PP_PathGenerator;
import org.github.ezauton.ezauton.pathplanning.Path;
import org.github.ezauton.ezauton.pathplanning.QuinticSpline;
import org.github.ezauton.ezauton.pathplanning.purepursuit.ILookahead;
import org.github.ezauton.ezauton.pathplanning.purepursuit.LookaheadBounds;
import org.github.ezauton.ezauton.pathplanning.purepursuit.PPWaypoint;
import org.github.ezauton.ezauton.pathplanning.purepursuit.PurePursuitMovementStrategy;
import org.github.ezauton.ezauton.recorder.base.PurePursuitRecorder;
import org.github.ezauton.ezauton.recorder.base.RobotStateRecorder;
import org.github.ezauton.ezauton.recorder.base.TankDriveableRecorder;
import org.github.ezauton.ezauton.robot.implemented.TankRobotTransLocDriveable;
import org.github.ezauton.ezauton.trajectory.geometry.ImmutableVector;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;

public class RecorderTest2
{
    public static void main(String[] args) throws IOException
    {

        ImmutableVector immutableVector = new ImmutableVector(0,0);
        immutableVector.isFinite();

        PPWaypoint waypoint1 = new PPWaypoint(new ImmutableVector(0, 0), 15, 13, -12);
        PPWaypoint waypoint2 = new PPWaypoint(new ImmutableVector(20, 20), 30, 13, -12);
        PPWaypoint waypoint3 = new PPWaypoint(new ImmutableVector(17.2, 12), 0, 13, -12);

        QuinticSpline spline = new QuinticSpline(waypoint1.getLocation(), waypoint2.getLocation(), new ImmutableVector(0, 30), new ImmutableVector(-20, 0));
        QuinticSpline spline2 = new QuinticSpline(waypoint2.getLocation(), waypoint3.getLocation(), new ImmutableVector(-20, 0), new ImmutableVector(0, -10));

        PPWaypoint[] ppWaypoints = QuinticSpline.toPathSegments(Arrays.asList(spline, spline2), Arrays.asList(waypoint1, waypoint2, waypoint3));

        Arrays.stream(ppWaypoints).forEach(System.out::println);
        Path path = new PP_PathGenerator(ppWaypoints).generate(0.05);
//
//        Path path = new PPWaypoint.Builder()
//                .add(0, 0, 16, 13, -12)
//                .add(0, 4, 16, 13, -12)
//                .add(-0.5, 8.589, 16, 13, -12)
//                .add(-0.5, 12.405, 13, 13, -12)
//                .add(-0.5, 17, 8.5, 13, -12)
//                .add(1.5, 19.4, 0, 13, -12)
//                .buildPathGenerator()
//                .generate(0.05);

        PurePursuitMovementStrategy ppMoveStrat = new PurePursuitMovementStrategy(path, 0.001);

        // Not a problem
        MultiThreadSimulation simulation = new MultiThreadSimulation(1);

        // Might be a problem
        SimulatedTankRobot robot = new SimulatedTankRobot(1, simulation.getClock(), 40, 0.3, 30D);

        TankRobotEncoderEncoderEstimator locEstimator = robot.getDefaultLocEstimator();
        locEstimator.reset();

        ILookahead lookahead = new LookaheadBounds(1, 3, 2, 10, locEstimator);

        TankRobotTransLocDriveable  tankRobotTransLocDriveable = robot.getDefaultTransLocDriveable();

        PPCommand ppCommand = new PPCommand(20, TimeUnit.MILLISECONDS, ppMoveStrat, locEstimator, lookahead, tankRobotTransLocDriveable);

        Recording recording = new Recording();

        RobotStateRecorder posRec = new RobotStateRecorder("robotstate", simulation.getClock(), locEstimator, locEstimator, robot.getLateralWheelDistance(), 1.5);
        PurePursuitRecorder ppRec = new PurePursuitRecorder("pp", simulation.getClock(), path, ppMoveStrat);
        TankDriveableRecorder tankRobot = new TankDriveableRecorder("td", simulation.getClock(), tankRobotTransLocDriveable);

        recording.addSubRecording(posRec);
        recording.addSubRecording(ppRec);
        recording.addSubRecording(tankRobot);

        BackgroundAction recAction = new BackgroundAction(10, TimeUnit.MILLISECONDS, recording);

        BackgroundAction updateKinematics = new BackgroundAction(2, TimeUnit.MILLISECONDS, robot);

        ActionGroup group = new ActionGroup()
                .with(updateKinematics)
                .with(recAction)
                .addSequential(ppCommand);

        simulation.add(group);


        // run the simulator with a timeout of 20 seconds
        simulation.run(30, TimeUnit.SECONDS);

        System.out.println("locEstimator.estimateLocation() = " + locEstimator.estimateLocation());

        System.out.println("about to save recording");
        // save recording
        {
            String homeDir = System.getProperty("user.home");
            java.nio.file.Path filePath = Paths.get(homeDir, ".ezauton", "splinelog.json");

            Files.createDirectories(filePath.getParent());

            BufferedWriter writer = Files.newBufferedWriter(filePath);
            String json = recording.toJson();

            writer.write(json);

            writer.close();

            JsonUtils.toObject(Recording.class, json);
        }
        System.out.println("saved  recording");

    }
}
