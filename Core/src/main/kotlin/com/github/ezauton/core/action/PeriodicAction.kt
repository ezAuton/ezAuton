package com.github.ezauton.core.action

import com.github.ezauton.conversion.Time
import com.github.ezauton.conversion.millis
import com.github.ezauton.conversion.now
import com.github.ezauton.core.action.require.combine
import com.github.ezauton.core.utils.RealClock
import com.github.ezauton.core.utils.Stopwatch
import kotlinx.coroutines.CancellationException

enum class ResourceManagement {
    UNTIL_FINISH,
    LET_GO_EACH_CYCLE
}

/**
 * An action which runs at recurring intervals 🔁. Will run all [Runnable]s sequentially every period timeUnit.
 */
abstract class PeriodicAction
/**
 * An action which runs at recurring intervals 🔁. Will run all [Runnable]s sequentially every period timeUnit.
 *
 * @param period
 */
(private val period: Time, private val resourceManagement: ResourceManagement = DEFAULT_RESOURCE_MANAGEMENT, private vararg val resourcePriorities: ResourcePriority) : Action {

    companion object {
        val DEFAULT_PERIOD = 20.millis
        val DEFAULT_RESOURCE_MANAGEMENT = ResourceManagement.UNTIL_FINISH
    }

    /**
     * A stopwatch which returns the time since the action started running (unless popped)
     *
     * @return
     */
    protected lateinit var stopwatch: Stopwatch
        private set
    /**
     * The action will attempt to try to run as close as it can to the given period.
     *
     * @return true if period is calculated after execution or false if the period counts execution time
     */

    /**
     * The action will attempt to try to run as close as it can to the given period.
     *
     * @param isPeriodDelayAfterExecution true if period is calculated after execution or false if the period counts execution time
     */
    var isPeriodDelayAfterExecution = false
    var timesRun = 0
        private set

    /**
     * @return If the action is finished. Will stop execution if returns true.
     */
    protected abstract fun isFinished(): Boolean

    /**
     * Creates a PeriodicAction which will run every 20 ms.
     */
    constructor() : this(DEFAULT_PERIOD)

    /**
     * Called when the periodic action is first initialized
     */
    @Throws(Exception::class)
    protected fun init() {
    }

    /**
     * Called every period cycle. By default, adds given [Runnable]s
     */
    @Throws(Exception::class)
    abstract fun execute()

    override suspend fun ActionContext.run() {

        val start = now()

        suspend fun doHold() = resourcePriorities.map { (resource, priority) ->
            resource.take(priority)
        }.combine()

        fun waitTime(): Time {
            val afterExecution = now()

            return if (isPeriodDelayAfterExecution) {
                period
            } else {
                val millisTotal = afterExecution - start
                timesRun++
                val expectedNext = period * timesRun
                expectedNext - millisTotal
            }
        }

        suspend fun doDelay() {
            val waitMillis = waitTime().millisL
            if (waitMillis < 0) {
                System.out.printf("The action is executing slower than the set period! milliseconds behind: %d\n", -waitMillis)
            } else if (waitMillis > 0) {
                delay(waitMillis)
            }
        }

        stopwatch = Stopwatch(RealClock.CLOCK)
        stopwatch.reset()

        suspend fun letGoEachCycle() {
            val held = doHold()
            init()
            var ranOnce = false
            do {
                if (!ranOnce) {
                    ranOnce = true
                } else held.giveBack()
                execute()
                held.giveBack()
                try {
                    doDelay()
                } catch (e: CancellationException) {
                    onInterrupted()
                    return
                }
            } while (!isFinished())
        }

        suspend fun untilFinish() {
            val held = doHold()
            init()
            do {
                execute()
                try {
                    doDelay()
                } catch (e: CancellationException) {
                    onInterrupted()
                    return
                }
                if (resourceManagement == ResourceManagement.LET_GO_EACH_CYCLE) held.giveBack()
            } while (!isFinished())
            held.giveBack()
        }

        when (resourceManagement) {
            ResourceManagement.LET_GO_EACH_CYCLE -> letGoEachCycle()
            ResourceManagement.UNTIL_FINISH -> untilFinish()
        }
    }

    /**
     * Called when the action is ended violently 💥
     *
     * @throws Exception
     */
    protected open fun onInterrupted() {}
}
