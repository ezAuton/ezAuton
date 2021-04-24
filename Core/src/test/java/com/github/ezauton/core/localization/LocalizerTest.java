package com.github.ezauton.core.localization;

import com.github.ezauton.core.action.ActionGroup;
import com.github.ezauton.core.action.BackgroundAction;
import com.github.ezauton.core.action.DelayedAction;
import com.github.ezauton.core.action.TimedPeriodicAction;
import com.github.ezauton.core.localization.estimators.EncoderRotationEstimator;
import com.github.ezauton.core.localization.estimators.TankRobotEncoderEncoderEstimator;
import com.github.ezauton.core.localization.sensors.TranslationalDistanceSensor;
import com.github.ezauton.core.simulation.SimulatedTankRobot;
import com.github.ezauton.core.simulation.TimeWarpedSimulation;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocalizerTest {
    @Test
    public void testThatTheLocalizersGiveSimilarResults() throws TimeoutException, ExecutionException {
        TimeWarpedSimulation sim = new TimeWarpedSimulation();
        SimulatedTankRobot simulatedBot = new SimulatedTankRobot(0.2, sim.getClock(), 3, -4, 4);
        simulatedBot.getDefaultLocEstimator().reset();

        TankRobotEncoderEncoderEstimator locEstimator = new TankRobotEncoderEncoderEstimator(simulatedBot.getLeftDistanceSensor(), simulatedBot.getRightDistanceSensor(), simulatedBot);
        EncoderRotationEstimator encRotEstimator = new EncoderRotationEstimator(locEstimator, new TranslationalDistanceSensor() {

            @Override
            public double getPosition() {
                return (simulatedBot.getLeftDistanceSensor().getPosition() + simulatedBot.getRightDistanceSensor().getPosition()) / 2; // correct because of linearity of integration
            }

            @Override
            public double getVelocity() {
                return (simulatedBot.getLeftDistanceSensor().getVelocity() + simulatedBot.getRightDistanceSensor().getVelocity()) / 2;
            }
        });
        SimpsonEncoderRotationEstimator simpson = new SimpsonEncoderRotationEstimator(locEstimator, () -> simulatedBot.getDefaultLocEstimator().getAvgTranslationalWheelVelocity(), sim.getClock());

        locEstimator.reset();
        encRotEstimator.reset();
        simpson.reset();


        ActionGroup actionGroup = new ActionGroup()
                .addParallel(new TimedPeriodicAction(5, TimeUnit.SECONDS, () -> simulatedBot.run(1, 1)))
                .with(new BackgroundAction(10, TimeUnit.MILLISECONDS, locEstimator::update, simulatedBot::update, encRotEstimator::update, simpson::update))
                .addSequential(new DelayedAction(7, TimeUnit.SECONDS));

        sim.add(actionGroup);

        sim.runSimulation(10, TimeUnit.SECONDS);

        simulatedBot.run(0, 0);

        System.out.println("TankEncoderEncoderRotationEstimator = " + locEstimator.estimateLocation());
        System.out.println("EncoderRotationEstimator = " + encRotEstimator.estimateLocation());
        System.out.println("SimpsonEncRotEstimator = " + simpson.estimateLocation());


        assertTrue(locEstimator.estimateLocation().dist2(encRotEstimator.estimateLocation()) < 0.01);
        assertTrue(simpson.estimateLocation().dist2(encRotEstimator.estimateLocation()) < 0.01);
        assertTrue(locEstimator.estimateLocation().dist2(simpson.estimateLocation()) < 0.01);


    }
}
