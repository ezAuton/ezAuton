package com.github.ezauton.core.test.simulator;

import com.github.ezauton.core.action.IAction;
import com.github.ezauton.core.action.TimedPeriodicAction;
import com.github.ezauton.core.simulation.ModernSimulatedClock;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InsantSimulatorTest {
    @Test
    public void testABC() throws TimeoutException {

        AtomicLong sum = new AtomicLong();

        IAction actionA = new TimedPeriodicAction(20, TimeUnit.SECONDS)
                .addRunnable(a -> () -> {
                    sum.addAndGet(a.getStopwatch().read());
                });

        IAction actionB = new TimedPeriodicAction(20, TimeUnit.SECONDS)
                .addRunnable(a -> () -> {
                    long l = sum.addAndGet(-a.getStopwatch().read(TimeUnit.MILLISECONDS));
                    assertEquals(0, l);
                });

        ModernSimulatedClock clock = new ModernSimulatedClock();

        clock
                .add(actionA)
                .add(actionB)
                .runSimulation(1000, TimeUnit.SECONDS);

        assertEquals(0, sum.get());
    }
}
