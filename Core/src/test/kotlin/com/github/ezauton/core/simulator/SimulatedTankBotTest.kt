package com.github.ezauton.core.simulator

import com.github.ezauton.conversion.*
import com.github.ezauton.core.action.*
import com.github.ezauton.core.localization.estimators.TankRobotEncoderEncoderEstimator
import com.github.ezauton.core.pathplanning.TrajectoryGenerator
import com.github.ezauton.core.pathplanning.purepursuit.LookaheadBounds
import com.github.ezauton.core.pathplanning.purepursuit.PPWaypoint
import com.github.ezauton.core.robot.implemented.TankRobotTransLocDrivable
import com.github.ezauton.core.simulation.SimulatedTankRobot
import com.github.ezauton.core.simulation.parallel
import com.github.ezauton.core.simulation.sequential
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException

class SimulatedTankBotTest {
  @Test
  @Throws(IOException::class, TimeoutException::class, ExecutionException::class)
  fun testStraight2() = runBlocking{

    val waypoint1 = PPWaypoint.simple2D(0.m, 0.m,0.mps, 3.mpss, (-4.0).mpss)
    val waypoint2 = PPWaypoint.simple2D(0.m, 6.m,1.mps, 3.mpss, (-4.0).mpss)
    val waypoint3 = PPWaypoint.simple2D(0.m, 20.m, 0.mps, 3.mpss, (-4.0).mpss)

    val pathGenerator = TrajectoryGenerator(waypoint1, waypoint2, waypoint3)

    val trajectory = pathGenerator.generate(0.05.seconds)

    val bot = SimulatedTankRobot.create(0.2.m, 3.0.mpss, 0.2.mps, 4.0.mps)

    val leftMotor = bot.leftMotor
    val rightMotor = bot.rightMotor

    val locEstimator = TankRobotEncoderEncoderEstimator.from(bot.leftDistanceSensor, bot.rightDistanceSensor, bot)
//    locEstimator.reset()


    val background = periodicAction(50.ms) {
      bot.update()
      locEstimator.update()
      return@periodicAction
    }

    val lookahead = LookaheadBounds(1.0.m, 5.0.m, 2.0.mps, 10.0.mps, locEstimator)

    val drivable = TankRobotTransLocDrivable(leftMotor, rightMotor, locEstimator, locEstimator, bot)

    val purePursuitAction = purePursuit(Period(50.ms), trajectory, locEstimator, drivable , lookahead, stopDistance = 8.m)

    val actionGroup = action {
      ephemeralScope {
        parallel(background)
        sequential(purePursuitAction)
      }
    }

    actionGroup.runWithTimeout(10.seconds)
  }

  @Test
  @Throws(TimeoutException::class, ExecutionException::class)
  fun testStraight() = runBlocking {

    val simulatedBot = SimulatedTankRobot.create(0.2.m, 3.0.mpss, (-4.0).mps, 4.0.mps)

    val actionGroup = action {
      parallel {
        periodic(duration = 5.seconds){
          simulatedBot.run(1.0.mps, 1.0.mps)
        }
      }
    }


    actionGroup.runWithTimeout(10.seconds)

    // stop the robot
    simulatedBot.run(0.0.mps, 0.0.mps)

    val estimatedLocation = simulatedBot.estimateLocation()

    println("estimated loc: $estimatedLocation")
    assertTrue(estimatedLocation.y > 4.5.m)
    assertTrue(estimatedLocation.y < 5.5.m)
    assertTrue(estimatedLocation.x.abs() < 0.1.m)

  }
}
