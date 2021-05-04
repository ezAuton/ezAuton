package com.github.ezauton.core.util

import com.github.ezauton.conversion.ms
import com.github.ezauton.core.action.delay
import com.github.ezauton.core.action.require.BaseResource
import com.github.ezauton.core.action.require.isOpen
import com.github.ezauton.core.action.require.isTaken
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class ResourceTest {

  @Test
  fun `test resource taking`() = runBlocking {

    val resource1 = BaseResource()
    val resource2 = BaseResource()
    val resource3 = BaseResource()

    resource1.dependOn(resource3)

    val resource = BaseResource()
      .dependOn(resource1)
      .dependOn(resource2)

    assertTrue(resource1.isOpen)
    assertTrue(resource3.isOpen)

    val hold = resource1.take()

    assertTrue(resource1.isTaken)
    assertTrue(resource3.isTaken)
    assertTrue(resource.isTaken)

    assertTrue(resource2.isOpen)

    hold.giveBack()


    assertAll("executables freed at end",
      { assertTrue(resource.isOpen) },
      { assertTrue(resource1.isOpen) },
      { assertTrue(resource2.isOpen) },
      { assertTrue(resource3.isOpen) },
    )

  }

  @Test
  fun `assert possession test`() = runBlocking {

    val resource = BaseResource()

    val job1 = launch {
      val hold = resource.ta
      delay(100.ms)

      hold.giveBack()
    }


    val job2 = launch {
      delay(50.ms)

      assertFalse(resource.hasPossession())
      assertTrue(resource.isOpen)

      resource.take()

      assertTrue(resource.hasPossession())
      assertFalse(resource.isOpen)
    }

    job1.join()
    job2.join()


    val e1 = job1Exception
    val e2 = job2Exception
//    requireNotNull(e1)
//    requireNotNull(e2)
//    assertTrue(e1)
//    assertTrue(e2)
//
//    assertTrue(canTake)
  }
}
