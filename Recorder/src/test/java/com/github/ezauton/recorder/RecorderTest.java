package com.github.ezauton.recorder;

import com.github.ezauton.core.action.ActionGroup;
import com.github.ezauton.core.action.BackgroundAction;
import com.github.ezauton.core.action.PurePursuitAction;
import com.github.ezauton.core.localization.estimators.TankRobotEncoderEncoderEstimator;
import com.github.ezauton.core.pathplanning.Path;
import com.github.ezauton.core.pathplanning.purepursuit.Lookahead;
import com.github.ezauton.core.pathplanning.purepursuit.LookaheadBounds;
import com.github.ezauton.core.pathplanning.purepursuit.PPWaypoint;
import com.github.ezauton.core.pathplanning.purepursuit.PurePursuitMovementStrategy;
import com.github.ezauton.core.robot.implemented.TankRobotTransLocDrivable;
import com.github.ezauton.core.simulation.SimulatedTankRobot;
import com.github.ezauton.core.simulation.TimeWarpedSimulation;
import com.github.ezauton.recorder.base.PurePursuitRecorder;
import com.github.ezauton.recorder.base.RobotStateRecorder;
import com.github.ezauton.recorder.base.TankDriveableRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RecorderTest {

    private Path path;
    private PurePursuitMovementStrategy ppMoveStrat;
    private TimeWarpedSimulation simulation;
    private SimulatedTankRobot robot;
    private TankRobotEncoderEncoderEstimator locEstimator;
    private Lookahead lookahead;
    private TankRobotTransLocDrivable tankRobotTransLocDriveable;
    private PurePursuitAction purePursuitAction;
    private BackgroundAction updateKinematics;
    private Recording recording;

    @BeforeEach
    public void init() {
        path = new PPWaypoint.Builder()
                .add(0, 0, 16, 13, -12)
                .add(0, 4, 16, 13, -12)
                .add(-0.5, 8.589, 16, 13, -12)
                .add(-0.5, 12.405, 13, 13, -12)
                .add(-0.5, 17, 8.5, 13, -12)
                .add(1.5, 19.4, 0, 13, -12)
                .buildPathGenerator()
                .generate(0.05);

        ppMoveStrat = new PurePursuitMovementStrategy(path, 0.001);

        // Not a problem
        simulation = new TimeWarpedSimulation(1);

        // Might be a problem
        robot = new SimulatedTankRobot(1, simulation.getClock(), 40, 0.3, 30D);

        locEstimator = robot.getDefaultLocEstimator();
        locEstimator.reset();

        lookahead = new LookaheadBounds(1, 7, 2, 10, locEstimator);

        tankRobotTransLocDriveable = robot.getDefaultTransLocDriveable();

        purePursuitAction = new PurePursuitAction(20, TimeUnit.MILLISECONDS, ppMoveStrat, locEstimator, lookahead, tankRobotTransLocDriveable);


        updateKinematics = new BackgroundAction(2, TimeUnit.MILLISECONDS, robot::update);
    }

    @Test
    public void testRecording() throws TimeoutException, ExecutionException {


        Recording recording = new Recording();
        RobotStateRecorder posRec = new RobotStateRecorder("robotstate", simulation.getClock(), locEstimator, locEstimator, robot.getLateralWheelDistance(), 1.5);
        PurePursuitRecorder ppRec = new PurePursuitRecorder("pp", simulation.getClock(), path, ppMoveStrat);
        TankDriveableRecorder tankRobot = new TankDriveableRecorder("td", simulation.getClock(), tankRobotTransLocDriveable);

        recording
                .addSubRecording(posRec)
                .addSubRecording(ppRec)
                .addSubRecording(tankRobot);

        BackgroundAction recAction = new BackgroundAction(10, TimeUnit.MILLISECONDS, recording::update);

        ActionGroup group = new ActionGroup()
                .with(updateKinematics)
                .with(recAction)
                .addSequential(purePursuitAction);

        simulation.add(group);


        // run the simulator with a timeout of 20 seconds
        simulation.runSimulation(30, TimeUnit.SECONDS);

        String json = recording.toJson();

        JsonUtils.toObject(Recording.class, json);

    }
}
