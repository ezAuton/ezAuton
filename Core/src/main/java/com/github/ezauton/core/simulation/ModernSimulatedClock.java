package com.github.ezauton.core.simulation;

import com.github.ezauton.core.action.IAction;
import com.github.ezauton.core.action.tangible.MainActionScheduler;
import com.github.ezauton.core.utils.IClock;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A Simulator which is which has no real world delay (and is thus non-blocking).
 * Can also operate as a clock, even outside the simulator.
 * Often can be slower than a warped time clock and does not account
 * for time spent to process tasks
 */
public class ModernSimulatedClock implements IClock, ISimulation {

    private final List<IAction> actions = new ArrayList<>();

    private final TreeMap<Long, Queue<Runnable>> timeToRunnableMap = new TreeMap<>();

    private long currentTime = 0;

    private boolean inSimulation = false;

    private AtomicBoolean currentActionRunning = new AtomicBoolean(false);

    private SimulationThread simulationThread;

    private Thread initThread;

    private MainActionScheduler mainActionScheduler = new MainActionScheduler(this);

    public ModernSimulatedClock() {
        simulationThread = new SimulationThread();
        initThread = Thread.currentThread();
    }

    /**
     * @return If an action either just started sleeping or just finished. Used to tell if we can run
     * the next action
     */
    private boolean isCurrentActionRunning() {
        return currentActionRunning.get();
    }

    @Override
    public long getTime() {
        return currentTime;
    }

    /**
     * Schedule a runnable. Runnables will be run before actions for the given time.
     *
     * @param millis   The timestamp at which the runnable should be run
     * @param runnable The thing to run
     */
    @Override
    public void scheduleAt(long millis, Runnable runnable) {
        Queue<Runnable> runnables = timeToRunnableMap.computeIfAbsent(millis, v -> new LinkedList<>());
        runnables.add(runnable);
    }

    public ModernSimulatedClock add(IAction action) {
        actions.add(action);
        return this;
    }

    @Override
    public void runSimulation(long timeout, TimeUnit timeUnit) throws TimeoutException {

        if (timeout <= 0) {
            throw new IllegalArgumentException("timeout must be positive");
        }

        simulationThread.start();

        try {
            simulationThread.join(timeUnit.toMillis(timeout));

            if (simulationThread.isAlive()) // Still joined after running
            {
                throw new TimeoutException("Thread ran out of time."); // TODO: this appropriate?
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Allows the action to sleep and notifies the ModernSimulatedClock we can now run the next action
     * (notifies action).
     *
     * @param dt
     * @param timeUnit
     */
    @Override
    public void sleep(long dt, TimeUnit timeUnit) throws InterruptedException {
        if (!inSimulation) {
            throw new IllegalStateException("You must be in a simulation to use sleep.");
        }
        if (Thread.currentThread() == initThread) {
            throw new IllegalStateException(("You cannot sleep from the thread which the clock was created on (use the simulator)"));
        }
        if (dt < 0) {
            throw new IllegalArgumentException("Wait time cannot be lower than 0");
        }
        if (dt == 0) {
            return;
        }

        long timeToStopSleep = getTime() + timeUnit.toMillis(dt);

        CountDownLatch countDownLatch = new CountDownLatch(1);

        countdownLatchAt(timeToStopSleep, countDownLatch);

        simulationThread.notifyCurrentActionPause();

        countDownLatch.await();
    }


    private void countdownLatchAt(long time, CountDownLatch countDownLatch) {
        synchronized (timeToRunnableMap) // TODO: why is synchronized here?
        {
            scheduleAt(time, () -> {
                countDownLatch.countDown();
                simulationThread.waitUntilCurrentActionPauses();
            });
        }
    }

    @Override
    public IClock getClock() {
        return this;
    }

    /**
     * The worker thread which executes the simulations
     */
    public class SimulationThread extends Thread {

        boolean stopNow = false;
        Set<Future<Void>> futures = new HashSet<>();


        SimulationThread() {
            super("Simulation Thread");
        }

        public void stopNow() {
            stopNow = true;
            futures.forEach(future -> future.cancel(true));
        }

        /**
         * Run all actions
         */
        @Override
        public synchronized void run() {
            inSimulation = true;
            doFirstActionCycle();
            runInitActionsUntilFinish();
            inSimulation = false;
        }

        /**
         * Initialize all actions and go through first run cycle. Note, we do not have to
         * run any {@link Runnable}s, since all {@link Runnable}s with a time equal to the current time
         * are run instantly
         */
        private void doFirstActionCycle() {
            for (IAction action : actions) {
                if (stopNow) break;
                // Once the action is finished, we can go on to the next action
                action.onFinish(this::notifyCurrentActionPause);


                futures.add(mainActionScheduler.scheduleAction(action));

                // waits until we can go on to the next action
                waitUntilCurrentActionPauses();
            }
        }

        /**
         * Runs actions until they are all finished after they are initialized with doFirstActionCycle()
         */
        private void runInitActionsUntilFinish() {
            while (!stopNow && !timeToRunnableMap.isEmpty()) {
                Map.Entry<Long, Queue<Runnable>> entry = timeToRunnableMap.pollFirstEntry();

                currentTime = entry.getKey();

                Queue<Runnable> runnables = entry.getValue();

                while (!runnables.isEmpty()) {
                    Runnable polled = runnables.poll();
                    if (polled != null) polled.run();
                }
            }
        }

        /**
         * Waits until the action is done processing for a current timestamp (either is done or is sleeping)
         */
        private void waitUntilCurrentActionPauses() {
            currentActionRunning.set(true);

            do {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            while (isCurrentActionRunning());
        }

        /**
         * Notify that an action just paused
         */
        synchronized void notifyCurrentActionPause() {
            currentActionRunning.set(false);
            notifyAll();
        }
    }
}
