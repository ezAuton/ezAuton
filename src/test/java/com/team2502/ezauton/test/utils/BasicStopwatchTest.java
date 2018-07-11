package com.team2502.ezauton.test.utils;

import com.team2502.ezauton.utils.RealStopwatch;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BasicStopwatchTest
{
    private static double DELTA = 1E-5;

    @Test
    public void testRead()
    {
        new Thread(() -> {
            RealStopwatch stopwatch = new RealStopwatch();
            try
            {
                Thread.sleep(1000);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            assertEquals(1, stopwatch.read(), 7E-3); // Delta of 5 ms
            try
            {
                Thread.sleep(1000);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            assertEquals(2, stopwatch.read(), 14E-3); // Delta of 10 ms
        }).start();
    }

    @Test
    public void testPop()
    {
        new Thread(() -> {
            RealStopwatch stopwatch = new RealStopwatch();
            try
            {
                Thread.sleep(1000);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            assertEquals(1, stopwatch.pop(), 5E-3); // Delta of 10 ms
            assertEquals(0, stopwatch.read(), 1E-3); // Delta of 1 ms
        }).start();
    }

    @Test
    public void testReset()
    {
        new Thread(() -> {
            RealStopwatch stopwatch = new RealStopwatch();
            try
            {
                Thread.sleep(1000);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            assertEquals(1000, stopwatch.read(), 5); // Delta of 10 ms
            stopwatch.reset();
            assertEquals(0, stopwatch.read(), 1); // Delta of 1 ms
        });
    }

    @Test
    public void testIsInit()
    {
        RealStopwatch stopwatch = new RealStopwatch();
        assertTrue(stopwatch.isInit());
    }
}
