package org.github.ezauton.ezauton.recorder;

import org.github.ezauton.ezauton.action.ActionGroup;
import org.github.ezauton.ezauton.action.BackgroundAction;
import org.github.ezauton.ezauton.action.PPCommand;
import org.github.ezauton.ezauton.action.simulation.MultiThreadSimulation;
import org.github.ezauton.ezauton.localization.estimators.TankRobotEncoderEncoderEstimator;
import org.github.ezauton.ezauton.pathplanning.PP_PathGenerator;
import org.github.ezauton.ezauton.pathplanning.Path;
import org.github.ezauton.ezauton.pathplanning.QuinticSpline;
import org.github.ezauton.ezauton.pathplanning.purepursuit.*;
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

        Path path = new SplinePPWaypoint.Builder()
                .add(0, 0, 0, 30, 15, 13, -12)
                .add(20, 20, -20, 0, 30, 13,-20)
                .add(17.2, 12, 0, -10, 0, 13, -20)
                .buildPathGenerator()
                .generate(0.05);

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
