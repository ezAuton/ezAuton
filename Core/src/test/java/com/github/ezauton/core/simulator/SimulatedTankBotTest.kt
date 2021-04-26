package com.github.ezauton.core.simulator

import com.github.ezauton.core.action.ActionGroup
import com.github.ezauton.core.action.DelayedAction
import com.github.ezauton.core.action.PurePursuitAction
import com.github.ezauton.core.action.TimedPeriodicAction
import com.github.ezauton.core.localization.estimators.TankRobotEncoderEncoderEstimator
import com.github.ezauton.core.pathplanning.PP_PathGenerator
import com.github.ezauton.core.pathplanning.purepursuit.LookaheadBounds
import com.github.ezauton.core.pathplanning.purepursuit.PPWaypoint
import com.github.ezauton.core.pathplanning.purepursuit.PurePursuitMovementStrategy
import com.github.ezauton.core.robot.implemented.TankRobotTransLocDrivable
import com.github.ezauton.core.simulation.SimulatedTankRobot
import com.github.ezauton.core.simulation.TimeWarpedSimulation
import com.github.ezauton.core.utils.TimeWarpedClock
import kotlinx.atomicfu.atomic
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.IOException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class SimulatedTankBotTest {
  @Test
  @Throws(IOException::class, TimeoutException::class, ExecutionException::class)
  fun testStraight2() {

    val test = atomic(1)

    val waypoint1 = PPWaypoint.simple2D(0.0, 0.0, 0.0, 3.0, -4.0)
    val waypoint2 = PPWaypoint.simple2D(0.0, 6.0, 1.0, 3.0, -4.0)
    val waypoint3 = PPWaypoint.simple2D(0.0, 20.0, 0.0, 3.0, -4.0)

    val pathGenerator = PP_PathGenerator(waypoint1, waypoint2, waypoint3)

    val path = pathGenerator.generate(0.05)

    val ppMoveStrat = PurePursuitMovementStrategy(path, 8)

    val clock = TimeWarpedClock(10.0)
    val bot = SimulatedTankRobot(0.2, clock, 3.0, 0.2, 4.0)
    bot.defaultLocEstimator.reset()
    val leftMotor = bot.leftMotor
    val rightMotor = bot.rightMotor

    val locEstimator = TankRobotEncoderEncoderEstimator(bot.leftDistanceSensor, bot.rightDistanceSensor, bot)
    locEstimator.reset()

    val sim = TimeWarpedSimulation(10.0)

    val background = BackgroundAction(50, TimeUnit.MILLISECONDS, Runnable { bot.update() }, Runnable { locEstimator.update() })

    val lookahead = LookaheadBounds(1.0, 5.0, 2.0, 10.0, locEstimator)

    val tankRobotTransLocDriveable = TankRobotTransLocDrivable(leftMotor, rightMotor, locEstimator, locEstimator, bot)

    val purePursuitAction = PurePursuitAction(50, TimeUnit.MILLISECONDS, ppMoveStrat, locEstimator, lookahead, tankRobotTransLocDriveable)

    val actionGroup = ActionGroup()
      .with(background)
      .addSequential(purePursuitAction)

    sim.add(actionGroup)

    sim.runSimulation(12, TimeUnit.SECONDS)
  }

  @Test
  @Throws(TimeoutException::class, ExecutionException::class)
  fun testStraight() {

    val sim = TimeWarpedSimulation()
    val simulatedBot = SimulatedTankRobot(0.2, sim.clock, 3.0, -4.0, 4.0)
    simulatedBot.defaultLocEstimator.reset()
    val locEstimator = TankRobotEncoderEncoderEstimator(simulatedBot.leftDistanceSensor, simulatedBot.rightDistanceSensor, simulatedBot)
    locEstimator.reset()

    //        sim.add(new TimedPeriodicAction(5, TimeUnit.SECONDS, () -> simulatedBot.run(1, 1)));
    //
    //        sim.add(new BackgroundAction(10, TimeUnit.MILLISECONDS, locEstimator::update, simulatedBot::update));

    val actionGroup = ActionGroup()
      .addParallel(TimedPeriodicAction(5, TimeUnit.SECONDS, { simulatedBot.run(1.0, 1.0) }))
      .with(BackgroundAction(10, TimeUnit.MILLISECONDS, Runnable { locEstimator.update() }, Runnable { simulatedBot.update() }))
      .addSequential(DelayedAction(7, TimeUnit.SECONDS))

    sim.add(actionGroup)

    sim.runSimulation(10, TimeUnit.SECONDS)

    simulatedBot.run(0.0, 0.0)

    val estimatedLocation = locEstimator.estimateLocation()

    assertTrue(estimatedLocation.get(1) > 5)
    assertTrue(Math.abs(estimatedLocation.get(0)) < 0.1)
  }
}
