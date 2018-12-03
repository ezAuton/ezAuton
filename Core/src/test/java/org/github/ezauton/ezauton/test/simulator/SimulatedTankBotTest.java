package org.github.ezauton.ezauton.test.simulator;

import org.github.ezauton.ezauton.action.BackgroundAction;
import org.github.ezauton.ezauton.action.PPCommand;
import org.github.ezauton.ezauton.action.TimedPeriodicAction;
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
import org.github.ezauton.ezauton.utils.TimeWarpedClock;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class SimulatedTankBotTest
{
    @Test
    public void testStraight2() throws IOException
    {
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

        MultiThreadSimulation sim = new MultiThreadSimulation(10);

        BackgroundAction background = new BackgroundAction(50, TimeUnit.MILLISECONDS, bot, locEstimator, () -> {
            if(bot.getLeftDistanceSensor().getVelocity() != 0)
            {
                System.out.println("leftVelocity() = " + bot.getLeftDistanceSensor().getVelocity());
                System.out.println("rightVelocity() = " + bot.getRightDistanceSensor().getVelocity());
            }
            return true;
        });

        sim.add(background);

        ILookahead lookahead = new LookaheadBounds(1, 5, 2, 10, locEstimator);

        TankRobotTransLocDriveable tankRobotTransLocDriveable = new TankRobotTransLocDriveable(leftMotor, rightMotor, locEstimator, locEstimator, bot);

        PPCommand ppCommand = new PPCommand(50, TimeUnit.MILLISECONDS, ppMoveStrat, locEstimator, lookahead, tankRobotTransLocDriveable);

        ppCommand.onFinish(background::end);
        ppCommand.onFinish(() -> bot.run(0, 0));

        sim.add(ppCommand);


        sim.run(12, TimeUnit.SECONDS);

        System.out.println("leftpos = " + bot.getLeftDistanceSensor().getPosition());
        System.out.println("rightpos = " + bot.getRightDistanceSensor().getPosition());

        System.out.println(locEstimator.estimateLocation());

        String homeDir = System.getProperty("user.home");
        java.nio.file.Path filePath = Paths.get(homeDir, ".ezauton", "log.txt");

        Files.createDirectories(filePath.getParent());

        BufferedWriter writer = Files.newBufferedWriter(filePath);
        writer.write(bot.log.toString());

        writer.close();

    }

    @Test
    public void testStraight()
    {
        TimeWarpedClock clock = new TimeWarpedClock(10);
        SimulatedTankRobot bot = new SimulatedTankRobot(0.2, clock, 3, -4, 4);

        MultiThreadSimulation sim = new MultiThreadSimulation();

        sim.add(new TimedPeriodicAction(5, TimeUnit.SECONDS, () -> {
            bot.run(1, 1);
            return true;
        }));

        sim.run(10, TimeUnit.SECONDS);

        bot.run(0, 0);

        System.out.println("leftpos = " + bot.getLeftDistanceSensor().getPosition());
        System.out.println("rightpos = " + bot.getRightDistanceSensor().getPosition());

    }
}
