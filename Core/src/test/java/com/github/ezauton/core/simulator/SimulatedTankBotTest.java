package com.github.ezauton.core.simulator;

import com.github.ezauton.core.action.*;
import com.github.ezauton.core.actuators.VelocityMotor;
import com.github.ezauton.core.localization.estimators.TankRobotEncoderEncoderEstimator;
import com.github.ezauton.core.pathplanning.PP_PathGenerator;
import com.github.ezauton.core.pathplanning.Path;
import com.github.ezauton.core.pathplanning.purepursuit.Lookahead;
import com.github.ezauton.core.pathplanning.purepursuit.LookaheadBounds;
import com.github.ezauton.core.pathplanning.purepursuit.PPWaypoint;
import com.github.ezauton.core.pathplanning.purepursuit.PurePursuitMovementStrategy;
import com.github.ezauton.core.robot.implemented.TankRobotTransLocDriveable;
import com.github.ezauton.core.simulation.SimulatedTankRobot;
import com.github.ezauton.core.simulation.TimeWarpedSimulation;
import com.github.ezauton.core.trajectory.geometry.ImmutableVector;
import com.github.ezauton.core.utils.TimeWarpedClock;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
        bot.getDefaultLocEstimator().reset();
        VelocityMotor leftMotor = bot.getLeftMotor();
        VelocityMotor rightMotor = bot.getRightMotor();

        TankRobotEncoderEncoderEstimator locEstimator = new TankRobotEncoderEncoderEstimator(bot.getLeftDistanceSensor(), bot.getRightDistanceSensor(), bot);
        locEstimator.reset();

        TimeWarpedSimulation sim = new TimeWarpedSimulation(10);

        BackgroundAction background = new BackgroundAction(50, TimeUnit.MILLISECONDS, bot::update, locEstimator::update);

        Lookahead lookahead = new LookaheadBounds(1, 5, 2, 10);

        TankRobotTransLocDriveable tankRobotTransLocDriveable = new TankRobotTransLocDriveable(leftMotor, rightMotor, locEstimator, locEstimator, bot);

        PurePursuitAction purePursuitAction = new PurePursuitAction(50, TimeUnit.MILLISECONDS, ppMoveStrat, locEstimator, locEstimator, lookahead, tankRobotTransLocDriveable);

        ActionGroup actionGroup = new ActionGroup()
                .with(background)
                .addSequential(purePursuitAction);

        sim.add(actionGroup);


        sim.runSimulation(12, TimeUnit.SECONDS);

    }

    @Test
    public void testStraight() throws TimeoutException, ExecutionException {

        TimeWarpedSimulation sim = new TimeWarpedSimulation();
        SimulatedTankRobot simulatedBot = new SimulatedTankRobot(0.2, sim.getClock(), 3, -4, 4);
        simulatedBot.getDefaultLocEstimator().reset();
        TankRobotEncoderEncoderEstimator locEstimator = new TankRobotEncoderEncoderEstimator(simulatedBot.getLeftDistanceSensor(), simulatedBot.getRightDistanceSensor(), simulatedBot);
        locEstimator.reset();

//        sim.add(new TimedPeriodicAction(5, TimeUnit.SECONDS, () -> simulatedBot.run(1, 1)));
//
//        sim.add(new BackgroundAction(10, TimeUnit.MILLISECONDS, locEstimator::update, simulatedBot::update));

        ActionGroup actionGroup = new ActionGroup()
                .addParallel(new TimedPeriodicAction(5, TimeUnit.SECONDS, () -> simulatedBot.run(1, 1)))
                .with(new BackgroundAction(10, TimeUnit.MILLISECONDS, locEstimator::update, simulatedBot::update))
                .addSequential(new DelayedAction(7, TimeUnit.SECONDS));

        sim.add(actionGroup);

        sim.runSimulation(10, TimeUnit.SECONDS);

        simulatedBot.run(0, 0);

        final ImmutableVector estimatedLocation = locEstimator.estimateLocation();

        assertTrue(estimatedLocation.get(1) > 5);
        assertTrue(Math.abs(estimatedLocation.get(0)) < 0.1);

    }
}
