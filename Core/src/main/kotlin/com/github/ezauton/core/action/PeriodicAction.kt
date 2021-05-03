package com.github.ezauton.core.action

import com.github.ezauton.conversion.Time
import com.github.ezauton.conversion.millis
import com.github.ezauton.conversion.now
import com.github.ezauton.core.action.require.ResourceHold
import com.github.ezauton.core.action.require.combine
import com.github.ezauton.core.simulation.WithCancel
import com.github.ezauton.core.utils.Stopwatch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

enum class ResourceManagement {
  UNTIL_FINISH,
  LET_GO_EACH_CYCLE
}

val DEFAULT_PERIOD = 20.millis
val DEFAULT_RESOURCE_MANAGEMENT = ResourceManagement.UNTIL_FINISH

enum class PeriodicResult {
  CONTINUE,
  STOP
}

interface PeriodicScope : CoroutineScope {
  val stopwatch: Stopwatch
  fun stop(): Nothing
  val start: Time
  val iteration: Int
}

private class PeriodicScopeImpl(val scope: CoroutineScope) : PeriodicScope, CoroutineScope by scope {
  override var iteration = 0
  override val stopwatch: Stopwatch = Stopwatch.new()

  init {
    stopwatch.init()
  }

  override val start = now()
  override fun stop(): Nothing {
    TODO()
  }
}

enum class DelayType {
  FROM_START,
  FROM_END,
}

data class PeriodicParams(
  val period: Time = DEFAULT_PERIOD,
  val loopMethod: DelayType = DelayType.FROM_START,
  val duration: Time? = null,
  val iterations: Int? = null,
  val resourceManagement: ResourceManagement = DEFAULT_RESOURCE_MANAGEMENT,
  val resourcePriorities: List<ResourcePriority> = emptyList(),
) {
  companion object {
    val DEFAULT = PeriodicParams()
  }
}

class PeriodicBuilder

suspend fun <T> periodic(params: PeriodicParams, block: suspend (PeriodicScope) -> T) = coroutineScope {
  periodic(params.period, params.loopMethod, params.duration, params.iterations, params.resourceManagement, *params.resourcePriorities.toTypedArray(), block = block)
}


suspend fun <T> periodic(
  period: Time = DEFAULT_PERIOD,
  loopMethod: DelayType = DelayType.FROM_START,
  duration: Time? = null,
  iterations: Int? = null,
  resourceManagement: ResourceManagement = DEFAULT_RESOURCE_MANAGEMENT,
  vararg resourcePriorities: ResourcePriority,
  block: suspend (PeriodicScope) -> T
): List<T> = coroutineScope {

  val list = ArrayList<T>()

  val state = PeriodicScopeImpl(this)

  suspend fun doHold(): ResourceHold {
    return when {
      resourcePriorities.isNotEmpty() -> {
        resourcePriorities.map { (resource, priority) ->
          resource.take(priority)
        }.combine()
      }

      else -> ResourceHold.Empty
    }

  }

  fun waitTime(): Time {
    val afterExecution = now()
    return when (loopMethod) {
      DelayType.FROM_START -> {
        val millisTotal = afterExecution - state.start
        state.iteration++
        val expectedNext = period * state.iteration
        expectedNext - millisTotal
      }
      DelayType.FROM_END -> period
    }
  }

  suspend fun doDelay() {
    val waitMillis = waitTime().millisL
    if (waitMillis < 0) {
      println("waitMillis neg 0 no delay")
      System.out.printf("The action is executing slower than the set period! milliseconds behind: %d\n", -waitMillis)
    } else if (waitMillis > 0) {
      delay(waitMillis)
    }
  }

  val stopwatch = Stopwatch.new().reset()

  fun isFinished(): Boolean {
    if (iterations != null && iterations == state.iteration) return true
    if (duration != null && duration <= stopwatch.read()) return true
    return false
  }

  suspend fun letGoEachCycle() {
    val held = doHold()
    var ranOnce = false
    do {
      if (!ranOnce) {
        ranOnce = true
      } else held.giveBack()
      val res = block(state)
      list.add(res)
      held.giveBack()
      try {
        doDelay()
      } catch (e: CancellationException) {
        held.giveBack()
        return
      }
    } while (!isFinished())
  }

  suspend fun untilFinish() {
    val held = doHold()
    do {
      val res = block(state)
      list.add(res)
      try {
        doDelay()
      } catch (e: CancellationException) {
        return
      } finally {
        held.giveBack()
      }
    } while (!isFinished())
  }

  try {
    when (resourceManagement) {
      ResourceManagement.LET_GO_EACH_CYCLE -> letGoEachCycle()
      ResourceManagement.UNTIL_FINISH -> untilFinish()
    }
  } catch (e: WithCancel) {
    if (!catchWith) throw e
  }

  return@coroutineScope list

}
