package com.team2502.ezauton.test.simulator;

import com.team2502.ezauton.command.ActionGroup;
import com.team2502.ezauton.command.BaseAction;
import com.team2502.ezauton.command.DelayedAction;
import com.team2502.ezauton.command.Simulation;
import com.team2502.ezauton.localization.estimators.TankRobotEncoderEncoderEstimator;
import com.team2502.ezauton.utils.SimulatedClock;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulatorTest
{

    @Test
    public void testSimpleAction()
    {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        Simulation simulation = new Simulation();
        simulation.add(new BaseAction(() -> atomicBoolean.set(true)));
        simulation.run(TimeUnit.SECONDS, 100);
        Assert.assertTrue(atomicBoolean.get());
    }

    @Test
    public void testDelayedAction()
    {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        Simulation simulation = new Simulation();
        DelayedAction delayedAction = new DelayedAction(TimeUnit.SECONDS, 1, () -> atomicBoolean.set(true));
        simulation.add(delayedAction);
        simulation.run(TimeUnit.SECONDS, 100);
        Assert.assertTrue(atomicBoolean.get());
    }

    @Test
    public void testActionGroup()
    {
        AtomicInteger atomicInteger = new AtomicInteger(0);

        Simulation simulation = new Simulation(10);
        ActionGroup actionGroup = new ActionGroup();

        DelayedAction delayedAction = new DelayedAction(TimeUnit.SECONDS, 1, () -> atomicInteger.compareAndSet(2, 3));
        delayedAction.onFinish(() -> System.out.println("1 done"));

        DelayedAction delayedAction2 = new DelayedAction(TimeUnit.MILLISECONDS, 10, () -> atomicInteger.compareAndSet(0, 1));
        delayedAction2.onFinish(() -> System.out.println("2 done"));

        DelayedAction delayedAction3 = new DelayedAction(TimeUnit.MILLISECONDS, 500, () -> atomicInteger.compareAndSet(1, 2));
        delayedAction3.onFinish(() -> System.out.println("3 done"));

        //TODO: Order matters? See github #35
        actionGroup.addParallel(delayedAction3); // second
        actionGroup.with(delayedAction2); // first
        actionGroup.addSequential(delayedAction); // last

        simulation.add(actionGroup);
        simulation.run(TimeUnit.SECONDS, 100);
        Assert.assertEquals(3, atomicInteger.get());
    }


    @Test
    public void testStraight()
    {
        SimulatedClock clock = new SimulatedClock();
        SimulatedTankRobot robot = new SimulatedTankRobot(1, clock, 14, 0.3, 16);
        TankRobotEncoderEncoderEstimator encoderRotationEstimator = new TankRobotEncoderEncoderEstimator(robot.getLeftDistanceSensor(), robot.getRightDistanceSensor(), robot);
        encoderRotationEstimator.reset();
        for(int i = 0; i < 1000; i++)
        {
            robot.run(1, 1);
            encoderRotationEstimator.update();
            clock.incAndGet();
        }
        System.out.println("encoderRotationEstimator = " + encoderRotationEstimator.estimateLocation());
    }
}
