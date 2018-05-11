package com.team2502.ezauton.test.utils;

import com.team2502.ezauton.utils.BasicStopwatch;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BasicStopwatchTest
{
    private static double DELTA = 1E-5;

    @Test
    public void testRead() throws InterruptedException
    {
        BasicStopwatch stopwatch = new BasicStopwatch();
        Thread.sleep(1000);
        assertEquals(1000, stopwatch.read(), 7); // Delta of 5 ms
        Thread.sleep(1000);
        assertEquals(2000, stopwatch.read(), 14); // Delta of 10 ms
    }

    @Test
    public void testPop() throws InterruptedException
    {
        BasicStopwatch stopwatch = new BasicStopwatch();
        Thread.sleep(1000);
        assertEquals(1000, stopwatch.pop(), 5); // Delta of 10 ms
        assertEquals(0, stopwatch.read(), 1); // Delta of 1 ms
    }

    @Test
    public void testReset() throws InterruptedException
    {
        BasicStopwatch stopwatch = new BasicStopwatch();
        Thread.sleep(1000);
        assertEquals(1000, stopwatch.read(), 5); // Delta of 10 ms
        stopwatch.reset();
        assertEquals(0, stopwatch.read(), 1); // Delta of 1 ms
    }

    @Test
    public void testIsInit() throws InterruptedException
    {
        BasicStopwatch stopwatch = new BasicStopwatch();
        assertTrue(stopwatch.isInit());
    }
}
