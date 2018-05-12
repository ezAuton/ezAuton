package com.team2502.ezauton.test.utils;

import com.team2502.ezauton.utils.BasicStopwatch;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BasicStopwatchTest
{
    private static double DELTA = 1E-5;

    @Test
    public void testRead()
    {
        new Thread (() -> {
            BasicStopwatch stopwatch = new BasicStopwatch();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            assertEquals(1000, stopwatch.read(), 7); // Delta of 5 ms
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            assertEquals(2000, stopwatch.read(), 14); // Delta of 10 ms
        }).start();
    }

    @Test
    public void testPop() {
        new Thread (() -> {
            BasicStopwatch stopwatch = new BasicStopwatch();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            assertEquals(1000, stopwatch.pop(), 5); // Delta of 10 ms
            assertEquals(0, stopwatch.read(), 1); // Delta of 1 ms
        }).start();
    }

    @Test
    public void testReset() {
        new Thread (() -> {
            BasicStopwatch stopwatch = new BasicStopwatch();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            assertEquals(1000, stopwatch.read(), 5); // Delta of 10 ms
            stopwatch.reset();
            assertEquals(0, stopwatch.read(), 1); // Delta of 1 ms
        });
    }

    @Test
    public void testIsInit() {
        BasicStopwatch stopwatch = new BasicStopwatch();
        assertTrue(stopwatch.isInit());
    }
}
