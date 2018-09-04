package com.team2502.ezauton.test.utils;

import com.team2502.ezauton.command.Simulation;
import com.team2502.ezauton.utils.Stopwatch;
import com.team2502.ezauton.utils.TimeWarpedClock;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TimeWarpedClockTest
{
    @Test
    public void testFastClock()
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
    public void testFastClockScheduling()
    {

        List<Number> expectedNums = Arrays.asList(1000, 2000, 3000, 4000, 5000);

        List<Number> nums = new ArrayList<>();
        TimeWarpedClock clock = new TimeWarpedClock(10);

        // schedule tasks to run at 1000 ms, 2000 ms, 3000 ms, 4000 ms, 5000 ms
        for(int i = 1; i <= 5; i++)
        {
            clock.scheduleAt(i * 1000, () -> nums.add(clock.getTime()));
        }

        clock.setStartTime(System.currentTimeMillis());

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
            assertEquals(expectedNums.get(i).doubleValue(), nums.get(i).doubleValue(), 50);
        }

    }

//    @Test
//    public void testFastClockSim()
//    {
//        Simulation sim = new Simulation();
//
//
//    }
}
