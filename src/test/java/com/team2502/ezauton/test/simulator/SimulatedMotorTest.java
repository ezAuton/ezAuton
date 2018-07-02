package com.team2502.ezauton.test.simulator;

import com.team2502.ezauton.actuators.InstantSimulatedMotor;
import com.team2502.ezauton.utils.SimulatedStopwatch;
import org.junit.Assert;
import org.junit.Test;

public class SimulatedMotorTest
{
    @Test
    public void testMotor()
    {
        SimulatedStopwatch stopwatch = new SimulatedStopwatch(1);
        InstantSimulatedMotor motor = new InstantSimulatedMotor(stopwatch);

        Assert.assertEquals(0, motor.getPosition(), 1E-6);
        motor.runVelocity(1);

        stopwatch.progress();
        Assert.assertEquals(1, motor.getPosition(), 1E-6);

        stopwatch.progress();
        Assert.assertEquals(2, motor.getPosition(), 1E-6);
        motor.runVelocity(10);
        Assert.assertEquals(2, motor.getPosition(), 1E-6);

        stopwatch.progress();
        Assert.assertEquals(12, motor.getPosition(), 1E-6);

    }
}
