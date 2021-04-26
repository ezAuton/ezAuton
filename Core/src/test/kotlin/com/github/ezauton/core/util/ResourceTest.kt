package com.github.ezauton.core.util

import com.github.ezauton.core.action.require.BaseResource
import com.github.ezauton.core.action.require.isTakenByAnyone
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.concurrent.thread

class ResourceTest {

  @Test
  fun `test resource taking`() {

    val resource1 = BaseResource()
    val resource2 = BaseResource()
    val resource3 = BaseResource()

    resource1.dependOn(resource3)

    val resource = BaseResource()
      .dependOn(resource1)
      .dependOn(resource2)

    val list = ArrayList<Boolean>()

    thread {
      runBlocking {
        list.add(!resource1.isTakenByAnyone)
        list.add(!resource3.isTakenByAnyone)

        val hold = resource1.take()

        list.add(resource1.isTakenByAnyone)
        list.add(resource3.isTakenByAnyone)
        list.add(resource.isTakenByAnyone)
        list.add(!resource2.isTakenByAnyone)

        hold.giveBack()
        val toAdd = arrayOf(resource, resource1, resource2, resource3).map { !it.isTakenByAnyone }
        list.addAll(toAdd)
      }
    }

    list.forEachIndexed { i, bool ->
      Assertions.assertTrue(bool, i.toString())
    }
  }

  @Test
  fun `assert possession test`() {

    val resource = BaseResource()

    var thread1NoException = true

    val thread1 = thread {
      runBlocking {
        val hold = resource.take()
        Thread.sleep(100)
        try {
          resource.assertPossession()
        } catch (e: IllegalStateException) {
          thread1NoException = false
        }
        hold.giveBack()
      }
    }

    var illegalStateCaught = false
    var canTake = false

    val thread2 = thread {

      Thread.sleep(50)
      try {
        resource.assertPossession()
      } catch (e: IllegalStateException) {
        illegalStateCaught = true
      }
      resource.take()
      canTake = true
    }

    thread1.join()
    thread2.join()

    assertTrue(thread1NoException)
    assertTrue(illegalStateCaught)
    assertTrue(canTake)
  }
}
