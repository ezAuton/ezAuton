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
  fun `overloaded periodic tries to become stable`() {
    var bool = false


    action {
      var someValue = 2;
      periodic(10.ms, duration = 10.seconds) { loop ->
        someValue *= someValue
        if (someValue > 1000) {
          loop.stop()
        }

        println("someValue is $someValue")
      }
    }


    var timesRun = 0

    val timedPeriodicAction = periodicAction(20.ms, duration = 2.seconds) {
      timesRun += 1
      if (!bool) {
        bool = true
        delay(1_000.ms)
      }
    }


    try {
      runBlocking {
        withTimeout(2_500.ms) {
          timedPeriodicAction.run()
        }
      }
    } catch (ignored: TimeoutException) {

    }

    assertEquals(2, timesRun)
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
  fun `action group of wrappers test`() = runBlocking {

    var counter = 0

    val actionGroup = action {

      parallel {
        delay(2.seconds)
        if (counter == 1) counter++
      }

      ephemeral {
        parallel {
          delay(3.seconds)
          if (counter == 0) counter++
        }

        delay(1.seconds)
      }
    }

    actionGroup.runWithTimeout(4.seconds)

    assertEquals(2, counter)

  }
}
