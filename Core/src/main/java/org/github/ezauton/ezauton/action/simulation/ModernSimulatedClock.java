package org.github.ezauton.ezauton.action.simulation;

import org.github.ezauton.ezauton.action.IAction;
import org.github.ezauton.ezauton.action.ThreadBuilder;
import org.github.ezauton.ezauton.utils.IClock;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ModernSimulatedClock implements IClock, ISimulation
{

    private final List<IAction> actions = new ArrayList<>();
    private final TreeMap<Long, Queue<CountDownLatch>> latchMap = new TreeMap<>();

    private long currentTime = 0;

    private AtomicBoolean actionBreak = new AtomicBoolean(false);

    private InnerThread innerThread;

    public class InnerThread extends Thread
    {
        public InnerThread()
        {
            super("Simulation Thread");
        }

        @Override
        public synchronized void run()
        {

            for(IAction action : actions)
            {
                action.onFinish(() -> {
                    actionBreak.set(true);
                    notifyBreak();
                });

                ThreadBuilder threadBuilder = new ThreadBuilder(action, ModernSimulatedClock.this);
                threadBuilder.start();

                waitForBreak();
            }

            while(!latchMap.isEmpty())
            {
                Map.Entry<Long, Queue<CountDownLatch>> entry = latchMap.pollFirstEntry();

                currentTime = entry.getKey();

                Queue<CountDownLatch> countDownLatches = entry.getValue();

                for(CountDownLatch countDownLatch : countDownLatches)
                {
                    countDownLatch.countDown();
                    waitForBreak();
                }
            }
        }

        private void waitForBreak()
        {
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
            while(!actionBreak.get());
        }

        public synchronized void notifyBreak()
        {
            actionBreak.set(true);
            notifyAll();
        }
    }

    public ModernSimulatedClock()
    {
        innerThread = new InnerThread();
    }

    @Override
    public long getTime()
    {
        return currentTime;
    }

    @Override
    public Future<?> scheduleAt(long millis, Runnable runnable)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ModernSimulatedClock add(IAction action)
    {
        actions.add(action);
        return this;
    }

    public void run(long timeout, TimeUnit timeUnit)
    {

        innerThread.start();

        try
        {
            innerThread.join(timeUnit.toMillis(timeout));
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void sleep(long dt, TimeUnit timeUnit)
    {
        if(dt <= 0)
        { return; }

        long timeIn = getTime() + dt;

        CountDownLatch countDownLatch = new CountDownLatch(1);

        synchronized(latchMap)
        {
            Queue<CountDownLatch> queue = latchMap.computeIfAbsent(timeIn, val -> new LinkedList<>());
            queue.add(countDownLatch);
        }

        actionBreak.set(true);

        innerThread.notifyBreak();

        try
        {
            countDownLatch.await();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public IClock getClock()
    {
        return this;
    }
}
