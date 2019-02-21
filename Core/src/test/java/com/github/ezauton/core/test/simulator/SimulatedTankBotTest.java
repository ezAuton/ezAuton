package com.github.ezauton.core.test.simulator;

import com.github.ezauton.core.action.BackgroundAction;
import com.github.ezauton.core.action.PPCommand;
import com.github.ezauton.core.action.TimedPeriodicAction;
import com.github.ezauton.core.actuators.IVelocityMotor;
import com.github.ezauton.core.localization.estimators.TankRobotEncoderEncoderEstimator;
import com.github.ezauton.core.pathplanning.PP_PathGenerator;
import com.github.ezauton.core.pathplanning.Path;
import com.github.ezauton.core.pathplanning.purepursuit.ILookahead;
import com.github.ezauton.core.pathplanning.purepursuit.LookaheadBounds;
import com.github.ezauton.core.pathplanning.purepursuit.PPWaypoint;
import com.github.ezauton.core.pathplanning.purepursuit.PurePursuitMovementStrategy;
import com.github.ezauton.core.robot.implemented.TankRobotTransLocDriveable;
import com.github.ezauton.core.simulation.SimulatedTankRobot;
import com.github.ezauton.core.simulation.TimeWarpedSimulation;
import com.github.ezauton.core.utils.TimeWarpedClock;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SimulatedTankBotTest {
    @Test
    public void testStraight2() throws IOException, TimeoutException, ExecutionException {
        PPWaypoint waypoint1 = PPWaypoint.simple2D(0, 0, 0, 3, -4);
        PPWaypoint waypoint2 = PPWaypoint.simple2D(0, 6, 1, 3, -4);
        PPWaypoint waypoint3 = PPWaypoint.simple2D(0, 20, 0, 3, -4);

        PP_PathGenerator pathGenerator = new PP_PathGenerator(waypoint1, waypoint2, waypoint3);

        Path path = pathGenerator.generate(0.05);

        PurePursuitMovementStrategy ppMoveStrat = new PurePursuitMovementStrategy(path, 8);

        TimeWarpedClock clock = new TimeWarpedClock(10);
        SimulatedTankRobot bot = new SimulatedTankRobot(0.2, clock, 3, 0.2, 4);

        IVelocityMotor leftMotor = bot.getLeftMotor();
        IVelocityMotor rightMotor = bot.getRightMotor();

        TankRobotEncoderEncoderEstimator locEstimator = new TankRobotEncoderEncoderEstimator(bot.getLeftDistanceSensor(), bot.getRightDistanceSensor(), bot);
        locEstimator.reset();

        TimeWarpedSimulation sim = new TimeWarpedSimulation(10);

        BackgroundAction background = new BackgroundAction(50, TimeUnit.MILLISECONDS, bot::update, locEstimator::update);

        sim.add(background);

        ILookahead lookahead = new LookaheadBounds(1, 5, 2, 10, locEstimator);

        TankRobotTransLocDriveable tankRobotTransLocDriveable = new TankRobotTransLocDriveable(leftMotor, rightMotor, locEstimator, locEstimator, bot);

        PPCommand ppCommand = new PPCommand(50, TimeUnit.MILLISECONDS, ppMoveStrat, locEstimator, lookahead, tankRobotTransLocDriveable);

        ppCommand.onFinish(background::end);
        ppCommand.onFinish(() -> bot.run(0, 0));

        sim.add(ppCommand);


        sim.runSimulation(12, TimeUnit.SECONDS);


        String homeDir = System.getProperty("user.home");
        java.nio.file.Path filePath = Paths.get(homeDir, ".ezauton", "log.txt");

        Files.createDirectories(filePath.getParent());

        BufferedWriter writer = Files.newBufferedWriter(filePath);
        writer.write(bot.log.toString());

        writer.close();

    }

    @Test
    public void testStraight() throws TimeoutException, ExecutionException {

        TimeWarpedSimulation sim = new TimeWarpedSimulation();
        SimulatedTankRobot bot = new SimulatedTankRobot(0.2, sim.getClock(), 3, -4, 4);
        TankRobotEncoderEncoderEstimator locEstimator = new TankRobotEncoderEncoderEstimator(bot.getLeftDistanceSensor(), bot.getRightDistanceSensor(), bot);
        locEstimator.reset();

        sim.add(new TimedPeriodicAction(5, TimeUnit.SECONDS, () -> bot.run(1, 1)));
        sim.add(new BackgroundAction(10, TimeUnit.MILLISECONDS, locEstimator::update, bot::update));
        sim.runSimulation(17, TimeUnit.SECONDS);

        bot.run(0, 0);

        System.out.println("bot. = " + locEstimator.estimateLocation());

    }
}
