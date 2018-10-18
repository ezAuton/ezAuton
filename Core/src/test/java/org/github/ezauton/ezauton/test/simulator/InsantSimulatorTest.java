package org.github.ezauton.ezauton.test.simulator;

import org.github.ezauton.ezauton.action.IAction;
import org.github.ezauton.ezauton.action.TimedPeriodicAction;
import org.github.ezauton.ezauton.action.simulation.ModernSimulatedClock;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class InsantSimulatorTest
{
    @Test
    public void testABC()
    {

        AtomicLong sum = new AtomicLong();

        IAction actionA = new TimedPeriodicAction(20, TimeUnit.SECONDS)
                .addUpdateable(a -> () -> {
                    sum.addAndGet(a.getStopwatch().read());
                    return true;
                });

        IAction actionB = new TimedPeriodicAction(20, TimeUnit.SECONDS)
                .addUpdateable(a -> () -> {
                    long l = sum.addAndGet(-a.getStopwatch().read(TimeUnit.MILLISECONDS));
                    Assert.assertEquals(0, l);
                    return true;
                });

        ModernSimulatedClock clock = new ModernSimulatedClock();

        clock
                .add(actionA)
                .add(actionB)
                .run(1000, TimeUnit.SECONDS);

        Assert.assertEquals(0, sum.get());
    }
}
