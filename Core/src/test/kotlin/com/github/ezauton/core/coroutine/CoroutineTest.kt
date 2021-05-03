package com.github.ezauton.core.coroutine

import com.github.ezauton.core.action.ephemeral
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CoroutineTest {

  @Test
  fun `test ephemeral`() = runBlocking {

    var counter = 0

    ephemeral {
      launch {
        repeat(1000) {
          counter += 1
          delay(100)
        }
      }

      delay(1000)

      assertEquals(10.0, counter.toDouble(), 1.0)

    }

  }
}
