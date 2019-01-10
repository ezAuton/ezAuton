package com.github.ezauton.core.test.utils;

import com.github.ezauton.core.action.DelayedAction;
import com.github.ezauton.core.action.simulation.MultiThreadSimulation;
import com.github.ezauton.core.utils.Stopwatch;
import com.github.ezauton.core.utils.TimeWarpedClock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;

public class TimeWarpedClockTest
{
    /**
     * @author https://stackoverflow.com/a/8301639
     */
    public class Retry implements TestRule
    {
        private int retryCount;

        public Retry(int retryCount) {
            this.retryCount = retryCount;
        }

        @Override
        public Statement apply(Statement base, Description description)
        {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable
                {
                    Throwable caughtThrowable = null;
                    for (int i = 0; i < retryCount; i++) {
                        try {
                            base.evaluate();
                            return;
                        } catch (Throwable t) {
                            caughtThrowable = t;
                            System.err.println(description.getDisplayName() + ": run " + (i+1) + " failed");
                        }
                    }
                    System.err.println(description.getDisplayName() + ": giving up after " + retryCount + " failures");
                    throw caughtThrowable;
                }
            };
        }
    }

    int i = 0;

    @Rule
    public Retry retry = new Retry(3);

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
        sim.runSimulation(10, TimeUnit.SECONDS);

//        System.out.println("time.get - init = " + (time.get() - init));

        assertEquals(1000, time.get() - init, 100);

    }
}
