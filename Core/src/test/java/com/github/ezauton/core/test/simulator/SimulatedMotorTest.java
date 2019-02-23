package com.github.ezauton.core.test.simulator;

import com.github.ezauton.core.action.Action;
import com.github.ezauton.core.action.BaseAction;
import com.github.ezauton.core.actuators.implementations.BaseSimulatedMotor;
import com.github.ezauton.core.simulation.ModernSimulatedClock;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimulatedMotorTest {
    @Test
    public void testMotor() throws TimeoutException {

        ModernSimulatedClock clock = new ModernSimulatedClock();

        Action action = new BaseAction(() -> {
            BaseSimulatedMotor motor = new BaseSimulatedMotor(clock);

            assertEquals(0, motor.getPosition(), 1E-6);
            motor.runVelocity(1);

            try {
                clock.sleep(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                return;
            }

            assertEquals(1, motor.getPosition(), 1E-6);

            try {
                clock.sleep(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                return;
            }
            assertEquals(2, motor.getPosition(), 1E-6);
            motor.runVelocity(10);
            assertEquals(2, motor.getPosition(), 1E-6);

            try {
                clock.sleep(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                return;
            }
            assertEquals(12, motor.getPosition(), 1E-6);
        });

        clock.add(action);
        clock.runSimulation(5, TimeUnit.SECONDS);

    }
}
