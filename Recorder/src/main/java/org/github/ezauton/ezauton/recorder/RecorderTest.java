package org.github.ezauton.ezauton.recorder;

import org.github.ezauton.ezauton.recorder.base.PurePursuitRecorder;
import org.github.ezauton.ezauton.recorder.base.RobotStateRecorder;
import org.github.ezauton.ezauton.action.ActionGroup;
import org.github.ezauton.ezauton.action.BackgroundAction;
import org.github.ezauton.ezauton.action.PPCommand;
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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class RecorderTest
{
    public static void main(String[] args) throws IOException
    {

        PPWaypoint waypoint1 = PPWaypoint.simple2D(0, 0, 0, 3, -4);
        PPWaypoint waypoint2 = PPWaypoint.simple2D(0, 6, 1, 3, -4);
        PPWaypoint waypoint3 = PPWaypoint.simple2D(0, 20, 0, 3, -4);

        PP_PathGenerator pathGenerator = new PP_PathGenerator(waypoint1, waypoint2, waypoint3);

        Path path = pathGenerator.generate(0.05);

        PurePursuitMovementStrategy ppMoveStrat = new PurePursuitMovementStrategy(path, 0.001);

//        ICopyable stopwatch = Simulation.getInstance().generateStopwatch();
        // Not a problem
        MultiThreadSimulation simulation = new MultiThreadSimulation(1);

        // Might be a problem
        SimulatedTankRobot robot = new SimulatedTankRobot(4, simulation.getClock(), 14, 0.3, 16D);

        IVelocityMotor leftMotor = robot.getLeftMotor();
        IVelocityMotor rightMotor = robot.getRightMotor();

        TankRobotEncoderEncoderEstimator locEstimator = new TankRobotEncoderEncoderEstimator(robot.getLeftDistanceSensor(), robot.getRightDistanceSensor(), robot);
        locEstimator.reset();

        ILookahead lookahead = new LookaheadBounds(1, 5, 2, 10, locEstimator);

        TankRobotTransLocDriveable tankRobotTransLocDriveable = new TankRobotTransLocDriveable(leftMotor, rightMotor, locEstimator, locEstimator, robot);

        PPCommand ppCommand = new PPCommand(20, TimeUnit.MILLISECONDS, ppMoveStrat, locEstimator, lookahead, tankRobotTransLocDriveable);

        Recording recording = new Recording();

        RobotStateRecorder posRec = new RobotStateRecorder("aaaaa", simulation.getClock(), locEstimator, locEstimator, robot.getLateralWheelDistance(), 5);
        PurePursuitRecorder  ppRec = new PurePursuitRecorder("bbbbb", simulation.getClock(), path, ppMoveStrat);

        recording.addSubRecording(posRec);
        recording.addSubRecording(ppRec);

        BackgroundAction ppRecAct = new BackgroundAction(20, TimeUnit.MILLISECONDS, ppRec);
        BackgroundAction posRecAct = new BackgroundAction(20, TimeUnit.MILLISECONDS, posRec);
        BackgroundAction updateKinematics = new BackgroundAction(2, TimeUnit.MILLISECONDS, robot);

        // Used to update the velocities of left and right motors while also updating the calculations for the location of the robot
        BackgroundAction backgroundAction = new BackgroundAction(20, TimeUnit.MILLISECONDS, locEstimator);

        ActionGroup group = new ActionGroup()
                .with(updateKinematics)
                .with(backgroundAction)
                .with(ppRecAct)
                .with(posRecAct)
                .addSequential(ppCommand);
        simulation
                .add(group);


        // run the simulator with a timeout of 20 seconds
        simulation.run(10, TimeUnit.SECONDS);

        // Save PP Recording separately
        {
            String homeDir = System.getProperty("user.home");
            java.nio.file.Path filePath = Paths.get(homeDir, ".ezauton", "log2.json");

            Files.createDirectories(filePath.getParent());

            BufferedWriter writer = Files.newBufferedWriter(filePath);
            String json = ppRec.toJson();

            writer.write(json);

            writer.close();

            JsonUtils.toObject(PurePursuitRecorder.class, json);
        }

        // save recording
        {
            String homeDir = System.getProperty("user.home");
            java.nio.file.Path filePath = Paths.get(homeDir, ".ezauton", "log.json");

            Files.createDirectories(filePath.getParent());

            BufferedWriter writer = Files.newBufferedWriter(filePath);
            String json = recording.toJson();

            writer.write(json);

            writer.close();

            JsonUtils.toObject(Recording.class, json);
        }



    }
}
