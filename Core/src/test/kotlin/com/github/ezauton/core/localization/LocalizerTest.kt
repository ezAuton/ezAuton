package com.github.ezauton.core.localization

import com.github.ezauton.conversion.*
import com.github.ezauton.core.action.action
import com.github.ezauton.core.action.delay
import com.github.ezauton.core.action.periodic
import com.github.ezauton.core.action.withTimeout
import com.github.ezauton.core.localization.estimators.EncoderRotationEstimator
import com.github.ezauton.core.localization.estimators.TankRobotEncoderEncoderEstimator
import com.github.ezauton.core.localization.sensors.TranslationalDistanceSensor
import com.github.ezauton.core.simulation.SimulatedTankRobot
import com.github.ezauton.core.simulation.parallel
import com.github.ezauton.core.utils.RealClock
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException

class LocalizerTest {

  @Test
  @Throws(TimeoutException::class, ExecutionException::class)
  fun testThatTheLocalizersGiveSimilarResults() {
    val simulatedBot = SimulatedTankRobot(0.2.meters, RealClock, 3.0.mps / sec, (-4.0).mps, 4.0.mps)
    simulatedBot.locationEstimator.reset()

    val locEstimator = TankRobotEncoderEncoderEstimator(simulatedBot.leftDistanceSensor, simulatedBot.rightDistanceSensor, simulatedBot)
    val encRotEstimator = EncoderRotationEstimator(locEstimator, object : TranslationalDistanceSensor {

      override // correct because of linearity of integration
      val position: Distance
        get() = (simulatedBot.leftDistanceSensor.position + simulatedBot.rightDistanceSensor.position) / 2

      override val velocity: LinearVelocity
        get() = (simulatedBot.leftDistanceSensor.velocity + simulatedBot.rightDistanceSensor.velocity) / 2
    })
    val simpson = SimpsonEncoderRotationEstimator(locEstimator, simulatedBot.locationEstimator, RealClock)

    locEstimator.reset()
    encRotEstimator.reset()
    simpson.reset()

    val actionGroup = action {

      parallel {
        delay(1.seconds)
        periodic(duration = 5.seconds) {
          simulatedBot.run(1.0.mps, 1.0.mps)
        }
      }

      val toUpdate = listOf(locEstimator, simulatedBot, encRotEstimator, simpson)
      periodic(duration = 7.seconds) {
        toUpdate.update()
      }

    }

    runBlocking {
      withTimeout(10.seconds) {
        actionGroup.run()
      }
    }


    simulatedBot.run(0.0.mps, 0.0.mps)

    assertTrue(locEstimator.estimateLocation().dist2(encRotEstimator.estimateLocation()) < 0.01.meters)
    assertTrue(simpson.estimateLocation().dist2(encRotEstimator.estimateLocation()) < 0.01.meters)
    assertTrue(locEstimator.estimateLocation().dist2(simpson.estimateLocation()) < 0.01.meters)

    // see if straight

    val x = locEstimator.estimateLocation().x
    val xAbs = x.abs()
    val y = locEstimator.estimateLocation().y

    // TODO: should go more straight
    assertTrue(xAbs < 0.5.meters){"$x has a magnitude larger than than 0.5 meters"}
    assertTrue(y> 1.meters){"$y is not less than 1 meter"}
  }
}
