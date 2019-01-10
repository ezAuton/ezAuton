package org.github.ezauton.ezauton.test.simulator;

import org.github.ezauton.ezauton.action.BaseAction;
import org.github.ezauton.ezauton.action.IAction;
import org.github.ezauton.ezauton.action.simulation.ModernSimulatedClock;
import org.github.ezauton.ezauton.actuators.implementations.BaseSimulatedMotor;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SimulatedMotorTest
{
    @Test
    public void testMotor() throws TimeoutException {

        ModernSimulatedClock clock = new ModernSimulatedClock();

        IAction action = new BaseAction(()->{
            BaseSimulatedMotor motor = new BaseSimulatedMotor(clock);

            Assert.assertEquals(0, motor.getPosition(), 1E-6);
            motor.runVelocity(1);

            try {
                clock.sleep(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                return;
            }

            Assert.assertEquals(1, motor.getPosition(), 1E-6);

            try {
                clock.sleep(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                return;
            }
            Assert.assertEquals(2, motor.getPosition(), 1E-6);
            motor.runVelocity(10);
            Assert.assertEquals(2, motor.getPosition(), 1E-6);

            try {
                clock.sleep(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                return;
            }
            Assert.assertEquals(12, motor.getPosition(), 1E-6);
        });

        clock.add(action);
        clock.runSimulation(5, TimeUnit.SECONDS);

    }
}
