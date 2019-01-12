package com.github.ezauton.core.test.utils;

import com.github.ezauton.core.actuators.Actuators;
import com.github.ezauton.core.actuators.IVelocityMotor;
import com.github.ezauton.core.actuators.IVoltageMotor;
import com.github.ezauton.core.actuators.implementations.BoundedVelocityProcessor;
import com.github.ezauton.core.actuators.implementations.RampUpVelocityProcessor;
import com.google.common.util.concurrent.AtomicDouble;
import com.github.ezauton.core.actuators.implementations.BaseSimulatedMotor;
import com.github.ezauton.core.utils.ManualClock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class ActuatorsTest
{
    @Test
    public void testSimpleVoltToVel()
    {
        AtomicDouble atomicDouble = new AtomicDouble();
        IVoltageMotor voltageMotor = atomicDouble::set;
        IVelocityMotor velocityMotor = Actuators.roughConvertVoltageToVel(voltageMotor, 16);

        velocityMotor.runVelocity(16);
        assertEquals(1D, atomicDouble.doubleValue(), 1E-6);

        velocityMotor.runVelocity(20);
        assertEquals(1D, atomicDouble.doubleValue(), 1E-6);

        velocityMotor.runVelocity(8);
        assertEquals(0.5D, atomicDouble.doubleValue(), 1E-6);

        velocityMotor.runVelocity(0);
        assertEquals(0, atomicDouble.doubleValue(), 1E-6);

        velocityMotor.runVelocity(-8);
        assertEquals(-0.5D, atomicDouble.doubleValue(), 1E-6);

        velocityMotor.runVelocity(-16);
        assertEquals(-1D, atomicDouble.doubleValue(), 1E-6);

        velocityMotor.runVelocity(-20);
        assertEquals(-1D, atomicDouble.doubleValue(), 1E-6);
    }

    @Test
    public void testRampUpVelocityProcessor()
    {
        AtomicDouble velocity = new AtomicDouble();
        IVelocityMotor velocityMotor = velocity::set;

        ManualClock clock = new ManualClock();

        RampUpVelocityProcessor velocityProcessor = new RampUpVelocityProcessor(velocityMotor, clock, 1);

        velocityProcessor.runVelocity(2);
        clock.addTime(1, TimeUnit.SECONDS);
        velocityProcessor.update();

        assertEquals(1, velocity.doubleValue(), 1E-6);

        clock.addTime(1, TimeUnit.SECONDS);
        velocityProcessor.update();

        assertEquals(2, velocity.doubleValue(), 1E-6);

        clock.addTime(1, TimeUnit.SECONDS);
        velocityProcessor.update();

        assertEquals(2, velocity.doubleValue(), 1E-6);

        velocityProcessor.runVelocity(1);
        clock.addTime(990, TimeUnit.MILLISECONDS);
        velocityProcessor.update();
        assertEquals(1.01D, velocity.doubleValue(), 1E-6);

        velocityProcessor.runVelocity(10);
        clock.addTime(990, TimeUnit.MILLISECONDS);
        velocityProcessor.update();
        assertEquals(2D, velocity.doubleValue(), 1E-6);
        assertEquals(2D, velocityProcessor.getLastVelocity(), 1E-6);
    }

    @Test
    public void testBoundedVelocityProcessor()
    {
        AtomicDouble velocity = new AtomicDouble();
        IVelocityMotor velocityMotor = velocity::set;

        BoundedVelocityProcessor velocityProcessor = new BoundedVelocityProcessor(velocityMotor, 16);

        velocityProcessor.runVelocity(1);
        assertEquals(1, velocity.doubleValue(), 1E-6);

        velocityProcessor.runVelocity(-15);
        assertEquals(-15, velocity.doubleValue(), 1E-6);

        velocityProcessor.runVelocity(-16);
        assertEquals(-16, velocity.doubleValue(), 1E-6);

        velocityProcessor.runVelocity(16);
        assertEquals(16, velocity.doubleValue(), 1E-6);

        velocityProcessor.runVelocity(-17);
        assertEquals(-16, velocity.doubleValue(), 1E-6);

        velocityProcessor.runVelocity(18);
        assertEquals(16, velocity.doubleValue(), 1E-6);

    }

    @Test
    public void testBoundedVelocityProcessorNegMaxSpeed()
    {
        AtomicDouble velocity = new AtomicDouble(0);

        assertThrows(IllegalArgumentException.class, () -> new BoundedVelocityProcessor(velocity::set, -10));
    }

    @Test
    public void testBaseSimulatedMotor()
    {
        AtomicDouble velocity = new AtomicDouble();
        IVelocityMotor velocityMotor = velocity::set;

        ManualClock clock = new ManualClock();
        BaseSimulatedMotor simulatedMotor = new BaseSimulatedMotor(clock);

        simulatedMotor.runVelocity(1);
        clock.addTime(1, TimeUnit.SECONDS);

        assertEquals(1, simulatedMotor.getPosition(), 1E-6);
        assertEquals(1, simulatedMotor.getVelocity(), 1E-6);

        clock.addTime(1, TimeUnit.SECONDS);

        assertEquals(2, simulatedMotor.getPosition(), 1E-6);
        assertEquals(1, simulatedMotor.getVelocity(), 1E-6);

        simulatedMotor.runVelocity(2);

        clock.addTime(1, TimeUnit.SECONDS);

        assertEquals(4, simulatedMotor.getPosition(), 1E-6);

        simulatedMotor.runVelocity(3);

        clock.addTime(1, TimeUnit.SECONDS);

        assertEquals(7, simulatedMotor.getPosition(), 1E-6);

        simulatedMotor.setSubscribed(velocityMotor);
        simulatedMotor.runVelocity(2);

        assertEquals(2, velocity.doubleValue(), 1E-6);
        assertSame(velocityMotor, simulatedMotor.getSubscribed());
    }
}
