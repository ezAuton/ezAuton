package org.github.ezauton.ezauton.action.simulation;

import org.github.ezauton.ezauton.action.IAction;
import org.github.ezauton.ezauton.action.ThreadBuilder;
import org.github.ezauton.ezauton.utils.IClock;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A Simulator which is which has no real world delay (and is thus non-blocking).
 * Can also operate as a clock, even outside the simulator.
 * Often can be slower than a warped time clock and does not account
 * for time spent to process tasks
 */
public class ModernSimulatedClock implements IClock, ISimulation
{

    private final List<IAction> actions = new ArrayList<>();

    private final TreeMap<Long, Queue<Runnable>> timeToRunnableMap = new TreeMap<>();

    private long currentTime = 0;

    private AtomicBoolean currentActionRunning = new AtomicBoolean(false);

    private SimulationThread simulationThread;

    private Thread initThread;

    /**
     *
     * @return If an action either just started sleeping or just finished. Used to tell if we can run
     * the next action
     */
    public boolean isCurrentActionRunning()
    {
        return currentActionRunning.get();
    }

    public ModernSimulatedClock()
    {
        simulationThread = new SimulationThread();
        initThread = Thread.currentThread();
    }

    @Override
    public long getTime()
    {
        return currentTime;
    }

    /**
     * Schedule a runnable. Runnables will be run before actions for the given time.
     * @param millis   The timestamp at which the runnable should be run
     * @param runnable The thing to run
     */
    @Override
    public void scheduleAt(long millis, Runnable runnable)
    {
        if(millis == getTime()) runnable.run();
        Queue<Runnable> runnables = timeToRunnableMap.computeIfAbsent(millis, v -> new LinkedList<>());
        runnables.add(runnable);
    }

    @Override
    public ModernSimulatedClock add(IAction action)
    {
        actions.add(action);
        return this;
    }

    @Override
    public void runSimulation(long timeout, TimeUnit timeUnit) throws TimeoutException {
        simulationThread.start();

        try
        {
            simulationThread.join(timeUnit.toMillis(timeout));

            if(simulationThread.isAlive()) // Still joined after running
            {
                throw new TimeoutException("Thread ran out of time."); // TODO: this appropriate?
            }
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Allows the action to sleep and notifies the ModernSimulatedClock we can now run the next action
     * (notifies action).
     * @param dt
     * @param timeUnit
     */
    @Override
    public void sleep(long dt, TimeUnit timeUnit) throws InterruptedException {
        if(Thread.currentThread() == initThread)
        {
            throw new IllegalStateException(("You cannot sleep from the thread which the clock was created on (use the simulator)"));
        }
        if(!(simulationThread.threads.contains(Thread.currentThread())))
        {
            throw new IllegalStateException("You can only sleep from actions in the simulation."); // TODO: might be able to remove
        }
        if(dt < 0)
        {
            throw new IllegalArgumentException("Wait time cannot be lower than 0");
        }
        if(dt == 0) { return; }

        long timeToStopSleep = getTime() + timeUnit.toMillis(dt);

        CountDownLatch countDownLatch = new CountDownLatch(1);

        countdownLatchAt(timeToStopSleep, countDownLatch);

        simulationThread.notifyCurrentActionPause();

        countDownLatch.await();
    }





    private void countdownLatchAt(long time, CountDownLatch countDownLatch)
    {
        synchronized(timeToRunnableMap) // TODO: why is synchronized here?
        {
            scheduleAt(time, () -> {
                countDownLatch.countDown();
                simulationThread.waitUntilCurrentActionPauses();
            });
        }
    }

    @Override
    public IClock getClock()
    {
        return this;
    }

    /**
     * The worker thread which executes the simulations
     */
    public class SimulationThread extends Thread
    {

        boolean stopNow = false;

        SimulationThread()
        {
            super("Simulation Thread");
        }


        public void stopNow()
        {
            stopNow = true;
            threads.forEach(Thread::interrupt);
        }

        Set<Thread> threads = new HashSet<>();

        /**
         * Run all actions
         */
        @Override
        public synchronized void run()
        {

            doFirstActionCycle();
            runInitActionsUntilFinish();
        }

        /**
         * Initialize all actions and go through first run cycle. Note, we do not have to
         * run any {@link Runnable}s, since all {@link Runnable}s with a time equal to the current time
         * are run instantly
         */
        private void doFirstActionCycle()
        {
            for(IAction action : actions)
            {
                if(stopNow) break;
                // Once the action is finished, we can go on to the next action
                action.onFinish(this::notifyCurrentActionPause);


                ThreadBuilder threadBuilder = new ThreadBuilder(action, ModernSimulatedClock.this);
                threads.add(threadBuilder.start());

                // waits until we can go on to the next action
                waitUntilCurrentActionPauses();
            }
        }

        /**
         * Runs actions until they are all finished after they are initialized with doFirstActionCycle()
         */
        private void runInitActionsUntilFinish()
        {
            while(!stopNow && !timeToRunnableMap.isEmpty())
            {
                Map.Entry<Long, Queue<Runnable>> entry = timeToRunnableMap.pollFirstEntry();

                currentTime = entry.getKey();

                Queue<Runnable> runnables = entry.getValue();

                for(Runnable runnable : runnables)
                {
                    runnable.run();
                }
            }
        }

        /**
         *  Waits until the action is done processing for a current timestamp (either is done or is sleeping)
         */
        private void waitUntilCurrentActionPauses()
        {
            currentActionRunning.set(true);

            do
            {
                try
                {
                    wait();
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            while(isCurrentActionRunning());
        }

        /**
         * Notify that an action just paused
         */
        synchronized void notifyCurrentActionPause()
        {
            currentActionRunning.set(false);
            notifyAll();
        }
    }
}
