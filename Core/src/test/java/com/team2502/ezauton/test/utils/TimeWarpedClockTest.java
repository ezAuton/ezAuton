package com.team2502.ezauton.test.utils;

import com.team2502.ezauton.command.Simulation;
import com.team2502.ezauton.utils.TimeWarpedClock;
import com.team2502.ezauton.utils.Stopwatch;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TimeWarpedClockTest
{
    @Test
    public void testTimeWarpedClock()
    {
        TimeWarpedClock clock = new TimeWarpedClock(10);

        Stopwatch stopwatch = new Stopwatch(clock);

        long initMillis = System.currentTimeMillis();

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
    public void testTimeWarpedClockScheduling()
    {

        List<Number> expectedNums = Arrays.asList(1000, 2000, 3000, 4000, 5000);

        List<Number> nums = new ArrayList<>();

        TimeWarpedClock clock = new TimeWarpedClock(10);
        clock.setStartTime(System.currentTimeMillis());
        // schedule tasks to run at 1000 ms, 2000 ms, 3000 ms, 4000 ms, 5000 ms
        for(int i = 1; i <= 5; i++)
        {
            clock.scheduleAt(i * 1000, () -> nums.add(clock.getTime()));
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

        for(int i = 0; i < expectedNums.size(); i++)
        {
            // There seems to be a delay that is quite large
            assertEquals(expectedNums.get(i).doubleValue(), nums.get(i).doubleValue() - 500, 100);
        }

    }

    @Test
    public void testTimeWarpedClockSim()
    {
        Simulation sim = new Simulation();


    }
}
