package com.github.ezauton.core.util

import com.github.ezauton.conversion.ms
import com.github.ezauton.conversion.seconds
import com.github.ezauton.core.action.*
import com.github.ezauton.core.simulation.parallel
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeoutException

class ActionTest {


  @Test
  fun `overloaded periodic tries to become stable`() = runBlocking{

    var bool = false
    var timesRun = 0

    val period = 20.ms
    val duration = 2.seconds

    val timedPeriodicAction = periodicAction(period, duration = duration) {
      timesRun += 1
      if (!bool) {
        bool = true
        delay(1_000.ms)
      }
    }


    try {
      timedPeriodicAction.runWithTimeout(2_500.ms)
    } catch (ignored: TimeoutException) {

    }

    val expected = duration / period

    assertEquals(expected, timesRun.toDouble(), 2.0)
  }

  @Test
  fun `action group of parallels test`() = runBlocking {

    var counter = 0


    val actionGroup = action {
      parallel { counter++ }
      parallel { counter++ }
      parallel { counter++ }
    }

    actionGroup.run()
    assertEquals(3, counter)
  }

  @Test
  fun `ephemeral actions test`() = runBlocking {

    var counter = 0

    val actionGroup = action {

      parallel {
        delay(3.seconds)
        if (counter == 1) counter++
      }

      ephemeralScope {
        parallel {
          delay(2.seconds)
          if (counter == 1) counter++
        }

        delay(1.seconds)
        if (counter == 0) counter++
      }
    }

    actionGroup.runWithTimeout(4.seconds)

    assertEquals(2, counter)

  }
}
