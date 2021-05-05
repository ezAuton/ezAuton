package com.github.ezauton.core.coroutine

import com.github.ezauton.core.action.ephemeralScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CoroutineTest {

  @Test
  fun `test ephemeral`() = runBlocking {


    val counter = ephemeralScope {

      var internalCounter = 0
      launch {
        repeat(1000) {
          internalCounter += 1
          delay(100)
        }
      }

      delay(1000)

      assertEquals(10.0, internalCounter.toDouble(), 1.0)

      internalCounter

    }

    assertEquals(10.0, counter.toDouble(), 1.0)

  }
}
