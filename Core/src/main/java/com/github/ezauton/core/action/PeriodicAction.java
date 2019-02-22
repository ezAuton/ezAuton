package com.github.ezauton.core.action;

import com.github.ezauton.core.utils.IClock;
import com.github.ezauton.core.utils.Stopwatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * An action which runs at recurring intervals 🔁. Will run all {@link Runnable}s sequentially every period timeUnit.
 */
public abstract class PeriodicAction extends BaseAction {

    protected final long periodMillis;
    private final List<Runnable> runnables;
    protected IClock clock;
    protected Stopwatch stopwatch;
    protected boolean periodDelayAfterExecution = false;
    private int timesRun = 0;

    /**
     * An action which runs at recurring intervals 🔁. Will run all {@link Runnable}s sequentially every period timeUnit.
     *
     * @param period
     * @param timeUnit
     * @param runnables
     */
    public PeriodicAction(long period, TimeUnit timeUnit, Runnable... runnables) {
        this.periodMillis = timeUnit.toMillis(period);
        this.runnables = new ArrayList<>(Arrays.asList(runnables));
    }

    /**
     * Creates a PeriodicAction which will run all {@link Runnable}s sequentially every 20 ms.
     *
     * @param runnables
     */
    public PeriodicAction(Runnable... runnables) {
        this(20, TimeUnit.MILLISECONDS, runnables);
    }

    /**
     * Creates a PeriodicAction which will run every 20 ms.
     */
    public PeriodicAction() {
        this(20, TimeUnit.MILLISECONDS);
    }


    /**
     * Add a runnable to tasks which are executed periodically
     *
     * @param runnable
     * @return
     */
    public PeriodicAction addRunnable(Runnable runnable) {
        runnables.add(runnable);
        return this;
    }

    public int getTimesRun() {
        return timesRun;
    }

    /**
     * An alternative to {@link PeriodicAction#addRunnable(Runnable)}.
     *
     * @param updateableFunc
     * @return
     * @see <a href = "https://stackoverflow.com/a/9584671/4889030">https://stackoverflow.com/a/9584671/4889030</a>
     */
    public PeriodicAction addRunnable(Function<PeriodicAction, Runnable> updateableFunc) {
        runnables.add(updateableFunc.apply(this));
        return this;
    }

    /**
     * Called when the periodic action is first initialized
     */
    protected void init() throws Exception  {
    }

    /**
     * Called every period cycle. By default, adds given {@link Runnable}s
     */
    protected void execute() throws Exception {
        runnables.forEach(Runnable::run);
    }

    /**
     * @return If the action is finished. Will stop execution if returns true.
     */
    protected abstract boolean isFinished() throws Exception;

    /**
     * The action will attempt to try to run as close as it can to the given period.
     *
     * @return true if period is calculated after execution or false if the period counts execution time
     */
    public boolean isPeriodDelayAfterExecution() {
        return periodDelayAfterExecution;
    }

    /**
     * The action will attempt to try to run as close as it can to the given period.
     *
     * @param periodDelayAfterExecution true if period is calculated after execution or false if the period counts execution time
     */
    public void setPeriodDelayAfterExecution(boolean periodDelayAfterExecution) {
        this.periodDelayAfterExecution = periodDelayAfterExecution;
    }

    @Override
    public final void run(ActionRunInfo actionRunInfo) throws Exception  {
        this.clock = actionRunInfo.getClock();

        stopwatch = new Stopwatch(clock);
        stopwatch.reset();

        long start = clock.getTime();

        init();
        do {
            execute();
            long afterExecution = clock.getTime();

            long wait;
            if (isPeriodDelayAfterExecution()) {
                wait = periodMillis;
            } else {
                long millisTotal = afterExecution - start;

                timesRun++;

                long expectedNext = periodMillis * timesRun;

                wait = expectedNext - millisTotal;
            }

            try {
                if (wait < 0) {
                    // TODO: probably should be an exception or a better way of displaying than this. (needs to be catchable though)
                    System.out.printf("The action is executing slower than the set period! milliseconds behind: %d\n", -wait);
                } else if (wait > 0) {
                    clock.sleep(wait, TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException e) {
                return;
            }
        }
        while (!isFinished() && !isStopped());
    }

    /**
     * A stopwatch which returns the time since the action started running (unless popped)
     * TODO Should probably have a stopwatch cannot be reset (which is public) and another one which can
     * TODO (and is protected) due to encapsulation
     *
     * @return
     */
    public final Stopwatch getStopwatch() {
        return stopwatch;
    }

    /**
     * The clock which the action is run by
     *
     * @return
     */
    public final IClock getClock() {
        return clock;
    }

}
