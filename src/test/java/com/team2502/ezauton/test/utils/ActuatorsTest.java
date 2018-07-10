package com.team2502.ezauton.test.utils;

import com.google.common.util.concurrent.AtomicDouble;
import com.team2502.ezauton.actuators.Actuators;
import com.team2502.ezauton.actuators.IVelocityMotor;
import com.team2502.ezauton.actuators.IVoltageMotor;
import com.team2502.ezauton.actuators.implementations.BaseSimulatedMotor;
import com.team2502.ezauton.actuators.implementations.BoundedVelocityProcessor;
import com.team2502.ezauton.actuators.implementations.RampUpVelocityProcessor;
import com.team2502.ezauton.utils.SimulatedStopwatch;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

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

        SimulatedStopwatch stopwatch = new SimulatedStopwatch(1);

        RampUpVelocityProcessor velocityProcessor = new RampUpVelocityProcessor(velocityMotor, stopwatch, 1);

        velocityProcessor.runVelocity(2);
        stopwatch.progress();
        velocityProcessor.update();

        assertEquals(1, velocity.doubleValue(), 1E-6);

        stopwatch.progress();
        velocityProcessor.update();

        assertEquals(2, velocity.doubleValue(), 1E-6);

        stopwatch.progress();
        velocityProcessor.update();

        assertEquals(2, velocity.doubleValue(), 1E-6);

        velocityProcessor.runVelocity(1);
        stopwatch.progress(0.99D);
        velocityProcessor.update();
        assertEquals(1.01D, velocity.doubleValue(), 1E-6);

        velocityProcessor.runVelocity(10);
        stopwatch.progress(0.99D);
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
    public void testBaseSimulatedMotor()
    {
        AtomicDouble velocity = new AtomicDouble();
        IVelocityMotor velocityMotor = velocity::set;

        SimulatedStopwatch stopwatch = new SimulatedStopwatch(1);
        BaseSimulatedMotor simulatedMotor = new BaseSimulatedMotor(stopwatch);

        simulatedMotor.runVelocity(1);
        stopwatch.progress(1);

        assertEquals(1,simulatedMotor.getPosition(),1E-6);
        assertEquals(1,simulatedMotor.getVelocity(),1E-6);

        stopwatch.progress(1);

        assertEquals(2,simulatedMotor.getPosition(),1E-6);
        assertEquals(1,simulatedMotor.getVelocity(),1E-6);

        simulatedMotor.runVelocity(2);

        stopwatch.progress(1);

        assertEquals(4,simulatedMotor.getPosition(),1E-6);

        simulatedMotor.runVelocity(3);

        stopwatch.progress(1);

        assertEquals(7,simulatedMotor.getPosition(),1E-6);

        simulatedMotor.setSubscribed(velocityMotor);
        simulatedMotor.runVelocity(2);

        assertEquals(2,velocity.doubleValue(),1E-6);
        assertSame(velocityMotor,simulatedMotor.getSubscribed());
    }
}
