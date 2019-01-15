package com.github.ezauton.core.test.simulator;

import com.github.ezauton.core.action.*;
import com.github.ezauton.core.localization.estimators.TankRobotEncoderEncoderEstimator;
import com.github.ezauton.core.simulation.TimeWarpedSimulation;
import com.github.ezauton.core.simulation.SimulatedTankRobot;
import com.github.ezauton.core.utils.ManualClock;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimulatorTest
{

    @Test
    public void testSimpleAction()
    {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        TimeWarpedSimulation simulation = new TimeWarpedSimulation();
        simulation.add(new BaseAction(() -> atomicBoolean.set(true)));
        simulation.runSimulation(100, TimeUnit.SECONDS);
        assertTrue(atomicBoolean.get());
    }

    @Test
    public void testDelayedAction()
    {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        TimeWarpedSimulation simulation = new TimeWarpedSimulation();
        DelayedAction delayedAction = new DelayedAction(1, TimeUnit.SECONDS, () -> atomicBoolean.set(true));
        simulation.add(delayedAction);
        simulation.runSimulation(100, TimeUnit.SECONDS);
        assertTrue(atomicBoolean.get());
    }

    @Test
    public void testActionGroup()
    {
        AtomicInteger atomicInteger = new AtomicInteger(0);

        TimeWarpedSimulation simulation = new TimeWarpedSimulation(10);
        ActionGroup actionGroup = new ActionGroup();

        DelayedAction delayedAction = new DelayedAction(1, TimeUnit.SECONDS, () -> atomicInteger.compareAndSet(2, 3));
        delayedAction.onFinish(() -> {});

        DelayedAction delayedAction2 = new DelayedAction(10, TimeUnit.MILLISECONDS, () -> atomicInteger.compareAndSet(0, 1));
        delayedAction2.onFinish(() -> {});

        DelayedAction delayedAction3 = new DelayedAction(500, TimeUnit.MILLISECONDS, () -> atomicInteger.compareAndSet(1, 2));
        delayedAction3.onFinish(() -> {});

        //TODO: Order matters? See github #35
        actionGroup.addParallel(delayedAction3); // second
        actionGroup.with(delayedAction2); // first
        actionGroup.addSequential(delayedAction); // last

        simulation.add(actionGroup);
        simulation.runSimulation(100, TimeUnit.SECONDS);
        assertEquals(3, atomicInteger.get());
    }


    @Test
    public void testStraight()
    {
        ManualClock clock = new ManualClock();
        SimulatedTankRobot robot = new SimulatedTankRobot(1, clock, 14, 0.3, 16);
        TankRobotEncoderEncoderEstimator encoderRotationEstimator = new TankRobotEncoderEncoderEstimator(robot.getLeftDistanceSensor(), robot.getRightDistanceSensor(), robot);
        encoderRotationEstimator.reset();
        for(int i = 0; i < 1000; i++)
        {
            robot.run(1, 1);
            encoderRotationEstimator.update();
            clock.incAndGet();
        }
//        System.out.println("encoderRotationEstimator = " + encoderRotationEstimator.estimateLocation());
    }

    @Test
    public void testTimeout() throws InterruptedException
    {
        AtomicInteger atomicInteger = new AtomicInteger(0);

        TimeWarpedSimulation simulation = new TimeWarpedSimulation(1);
        ActionGroup actionGroup = new ActionGroup();

        PeriodicAction action = new BackgroundAction(20, TimeUnit.MILLISECONDS, atomicInteger::incrementAndGet);

        //TODO: Order matters? See github #35
        actionGroup.addSequential(action);

        simulation.add(actionGroup);
        simulation.runSimulation(1, TimeUnit.SECONDS);

        int actual = atomicInteger.get();
        assertEquals(50, actual, 2);
        Thread.sleep(500); // other threads should have stopped, no more incrementing
//        assertEquals(2, actual, atomicInteger.get());
    }
}
