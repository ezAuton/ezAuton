package com.github.ezauton.core.action

import com.github.ezauton.core.Duration
import com.github.ezauton.core.millis
import com.github.ezauton.core.utils.RealClock
import com.github.ezauton.core.utils.Stopwatch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import java.util.*
import java.util.function.Function

/**
 * An action which runs at recurring intervals 🔁. Will run all [Runnable]s sequentially every period timeUnit.
 */
abstract class PeriodicAction
/**
 * An action which runs at recurring intervals 🔁. Will run all [Runnable]s sequentially every period timeUnit.
 *
 * @param period
 * @param timeUnit
 * @param runnables
 */
(period: Duration, vararg runnables: Runnable) : BaseAction() {

    companion object {
        val DEFAULT_PERIOD = 20.millis
    }

    private val periodMillis: Long = period.millis
    private val runnables: MutableList<Runnable>

    /**
     * A stopwatch which returns the time since the action started running (unless popped)
     * TODO Should probably have a stopwatch cannot be reset (which is public) and another one which can
     * TODO (and is protected) due to encapsulation
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
     * @param periodDelayAfterExecution true if period is calculated after execution or false if the period counts execution time
     */
    var isPeriodDelayAfterExecution = false
    var timesRun = 0
        private set

    /**
     * @return If the action is finished. Will stop execution if returns true.
     */
    protected abstract fun isFinished(): Boolean

    init {
        this.runnables = ArrayList(Arrays.asList(*runnables))
    }

    /**
     * Creates a PeriodicAction which will run all [Runnable]s sequentially every 20 ms.
     *
     * @param runnables
     */
    constructor(vararg runnables: Runnable) : this(DEFAULT_PERIOD, *runnables)

    /**
     * Creates a PeriodicAction which will run every 20 ms.
     */
    constructor() : this(DEFAULT_PERIOD)


    /**
     * Add a runnable to tasks which are executed periodically
     *
     * @param runnable
     * @return
     */
    fun addRunnable(runnable: Runnable): PeriodicAction {
        runnables.add(runnable)
        return this
    }

    /**
     * An alternative to [PeriodicAction.addRunnable].
     *
     * @param updateableFunc
     * @return
     * @see [https://stackoverflow.com/a/9584671/4889030](https://stackoverflow.com/a/9584671/4889030)
     */
    fun addRunnable(updateableFunc: Function<PeriodicAction, Runnable>): PeriodicAction {
        runnables.add(updateableFunc.apply(this))
        return this
    }

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
    protected open fun execute() {
        runnables.forEach { it.run() }
    }

    override suspend fun run() {
        val clock = RealClock.CLOCK

        stopwatch = Stopwatch(clock)
        stopwatch.reset()

        val start = clock.time

        init()
        do {
            execute()
            val afterExecution = clock.time

            val wait = if (isPeriodDelayAfterExecution) {
                periodMillis
            } else {
                val millisTotal = afterExecution - start

                timesRun++

                val expectedNext = periodMillis * timesRun

                expectedNext - millisTotal
            }

            try {
                if (wait < 0) {
                    System.out.printf("The action is executing slower than the set period! milliseconds behind: %d\n", -wait)
                } else if (wait > 0) {
                    delay(wait)
                }
            } catch (e: CancellationException) {
                loopWaitInterrupted()
                return
            }

        } while (!isFinished())
    }

    /**
     * Called when the action is ended violently 💥
     *
     * @throws Exception
     */
    protected open fun loopWaitInterrupted() {}

}
