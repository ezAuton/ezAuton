package org.github.ezauton.ezauton.test.utils;

import org.github.ezauton.ezauton.action.DelayedAction;
import org.github.ezauton.ezauton.action.simulation.MultiThreadSimulation;
import org.github.ezauton.ezauton.utils.Stopwatch;
import org.github.ezauton.ezauton.utils.TimeWarpedClock;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;

public class TimeWarpedClockTest
{
    @Test
    public void testFastClock()
    {
        TimeWarpedClock clock = new TimeWarpedClock(10);

        Stopwatch stopwatch = new Stopwatch(clock);

        stopwatch.resetIfNotInit();

        try
        {
            Thread.sleep(5);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }

        assertEquals(50, stopwatch.pop(), 15);
    }

    @Test
    public void testFastClockScheduling()
    {

        List<Number> expectedNums = Arrays.asList(1000, 2000, 3000, 4000, 5000);

        List<Number> nums = new ArrayList<>();
        TimeWarpedClock clock = new TimeWarpedClock(10, -70); // -70 because my laptop usually takes 70ms

        // schedule tasks to run at 1000 ms, 2000 ms, 3000 ms, 4000 ms, 5000 ms
        for(int i = 1; i <= 5; i++)
        {
            clock.scheduleIn(i * 1000, TimeUnit.MILLISECONDS, () -> nums.add(clock.getTime()));
        }

        try
        {
            Thread.sleep(550); // 5000 fake ms = 500 real ms, throw in 50 extra to be safe
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }

        assertEquals(expectedNums.size(), nums.size());

        System.out.println("expectedNums = " + expectedNums);
        System.out.println("nums = " + nums);
        for(int i = 0; i < expectedNums.size(); i++)
        {
            assertEquals(expectedNums.get(i).doubleValue(), nums.get(i).doubleValue(), 100);
        }

    }

    @Test
    public void testFastClockWait()
    {
        AtomicLong time = new AtomicLong(0);

        int speed = 100000;
        MultiThreadSimulation sim = new MultiThreadSimulation(speed);

        // 1000 fake seconds * 1 real sec / `1000 fake secs = 1 real sec
        sim.add(new DelayedAction(speed, TimeUnit.SECONDS, () -> time.set(System.currentTimeMillis())));
        long init = System.currentTimeMillis();
        sim.run(10, TimeUnit.SECONDS);

        System.out.println("time.get - init = " + (time.get() - init));

        assertEquals(1000, time.get() - init, 100);

    }
}
