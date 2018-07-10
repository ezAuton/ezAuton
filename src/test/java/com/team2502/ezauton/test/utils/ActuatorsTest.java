package com.team2502.ezauton.test.utils;

import com.google.common.util.concurrent.AtomicDouble;
import com.team2502.ezauton.actuators.Actuators;
import com.team2502.ezauton.actuators.IVelocityMotor;
import com.team2502.ezauton.actuators.IVoltageMotor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActuatorsTest
{
    @Test
    public void testSimpleVoltToVel()
    {
        AtomicDouble atomicDouble = new AtomicDouble();
        IVoltageMotor voltageMotor = atomicDouble::set;
        IVelocityMotor velocityMotor = Actuators.roughConvertVoltageToVel(voltageMotor, 16);

        velocityMotor.runVelocity(16);
        assertEquals(1D,atomicDouble.doubleValue(),1E-6);

        velocityMotor.runVelocity(20);
        assertEquals(1D,atomicDouble.doubleValue(),1E-6);

        velocityMotor.runVelocity(8);
        assertEquals(0.5D,atomicDouble.doubleValue(),1E-6);

        velocityMotor.runVelocity(0);
        assertEquals(0,atomicDouble.doubleValue(),1E-6);

        velocityMotor.runVelocity(-8);
        assertEquals(-0.5D,atomicDouble.doubleValue(),1E-6);

        velocityMotor.runVelocity(-16);
        assertEquals(-1D,atomicDouble.doubleValue(),1E-6);

        velocityMotor.runVelocity(-20);
        assertEquals(-1D,atomicDouble.doubleValue(),1E-6);
    }
}
