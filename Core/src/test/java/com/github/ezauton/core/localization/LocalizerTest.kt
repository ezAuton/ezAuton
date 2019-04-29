package com.github.ezauton.core.localization

import com.github.ezauton.core.action.ActionGroup
import com.github.ezauton.core.action.BackgroundAction
import com.github.ezauton.core.action.DelayedAction
import com.github.ezauton.core.action.TimedPeriodicAction
import com.github.ezauton.core.localization.SimpsonEncoderRotationEstimator
import com.github.ezauton.core.localization.estimators.EncoderRotationEstimator
import com.github.ezauton.core.localization.estimators.TankRobotEncoderEncoderEstimator
import com.github.ezauton.core.localization.sensors.TranslationalDistanceSensor
import com.github.ezauton.core.simulation.SimulatedTankRobot
import com.github.ezauton.core.simulation.TimeWarpedSimulation
import com.github.ezauton.core.trajectory.geometry.ImmutableVector
import com.github.ezauton.core.utils.Stopwatch
import javafx.scene.paint.Stop
import org.junit.jupiter.api.Test

import java.beans.Encoder
import java.sql.SQLOutput
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

import org.junit.jupiter.api.Assertions.assertTrue

class LocalizerTest {
    @Test
    @Throws(TimeoutException::class, ExecutionException::class)
    fun testThatTheLocalizersGiveSimilarResults() {
        val sim = TimeWarpedSimulation()
        val simulatedBot = SimulatedTankRobot(0.2, sim.clock, 3.0, -4.0, 4.0)
        simulatedBot.defaultLocEstimator.reset()

        val locEstimator = TankRobotEncoderEncoderEstimator(simulatedBot.leftDistanceSensor, simulatedBot.rightDistanceSensor, simulatedBot)
        val encRotEstimator = EncoderRotationEstimator(locEstimator, object : TranslationalDistanceSensor {

            override// correct because of linearity of integration
            val position: Double
                get() = (simulatedBot.leftDistanceSensor.position + simulatedBot.rightDistanceSensor.position) / 2

            override val velocity: Double
                get() = (simulatedBot.leftDistanceSensor.velocity + simulatedBot.rightDistanceSensor.velocity) / 2
        })
        val simpson = SimpsonEncoderRotationEstimator(locEstimator, { simulatedBot.defaultLocEstimator.avgTranslationalWheelVelocity }, sim.clock)

        locEstimator.reset()
        encRotEstimator.reset()
        simpson.reset()


        val actionGroup = ActionGroup()
                .addParallel(TimedPeriodicAction(5, TimeUnit.SECONDS, { simulatedBot.run(1.0, 1.0) }))
                .with(BackgroundAction(10, TimeUnit.MILLISECONDS, Runnable { locEstimator.update() }, Runnable { simulatedBot.update() }, Runnable { encRotEstimator.update() }, Runnable { simpson.update() }))
                .addSequential(DelayedAction(7, TimeUnit.SECONDS))

        sim.add(actionGroup)

        sim.runSimulation(10, TimeUnit.SECONDS)

        simulatedBot.run(0.0, 0.0)

        println("TankEncoderEncoderRotationEstimator = " + locEstimator.estimateLocation())
        println("EncoderRotationEstimator = " + encRotEstimator.estimateLocation()!!)
        println("SimpsonEncRotEstimator = " + simpson.estimateLocation()!!)


        assertTrue(locEstimator.estimateLocation().dist2(encRotEstimator.estimateLocation()!!) < 0.01)
        assertTrue(simpson.estimateLocation()!!.dist2(encRotEstimator.estimateLocation()!!) < 0.01)
        assertTrue(locEstimator.estimateLocation().dist2(simpson.estimateLocation()!!) < 0.01)


    }
}
