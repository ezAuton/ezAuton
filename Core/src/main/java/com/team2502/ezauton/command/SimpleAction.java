package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.IClock;
import com.team2502.ezauton.utils.Stopwatch;

import java.util.concurrent.TimeUnit;

public abstract class SimpleAction extends AbstractAction
{

    private final long periodMillis;
    private IClock clock;
    private Stopwatch stopwatch;

    public SimpleAction(TimeUnit timeUnit, long period)
    {
        this.periodMillis = timeUnit.toMillis(period);
    }

    protected void init() {}

    protected void execute() {}

    protected abstract boolean isFinished();

    @Override
    public void run(IClock clock)
    {
        this.clock = clock;

        stopwatch = new Stopwatch(clock);
        stopwatch.reset();

        init();
        do
        {
            execute();
            try
            {
                clock.sleep(TimeUnit.MILLISECONDS, periodMillis);
            }
            catch(InterruptedException e)
            {
                return;
            }
        }
        while(!isFinished() && !isStopped());
    }

    public Stopwatch getStopwatch()
    {
        return stopwatch;
    }

    public IClock getClock()
    {
        return clock;
    }

//    /**
//     * @return A Thread which acts like a command but can be run faster
//     */
//    @Override
//    public Thread buildThread(long millisPeriod)
//    {
//        if(used)
//        {
//            throw new RuntimeException("Action already used!");
//        }
//        used = true;
//        return new Thread(() -> {
//            RealStopwatch stopwatch = new RealStopwatch();
//            init(stopwatch);
//            while(!isFinished())
//            {
//                execute();
//                try
//                {
//                    Thread.sleep(millisPeriod);
//                }
//                catch(InterruptedException e)
//                {
//                    e.printStackTrace();
//                }
//            }
//            runnables.forEach(Runnable::run);
//        });
//    }

//    @Override
//    public SimpleAction onFinish(Runnable onFinish)
//    {
//        runnables.add(onFinish);
//        return this;
//    }
//
//    @Override
//    public void simulate(long millisPeriod)
//    {
//        if(used)
//        {
//            throw new RuntimeException("Action already used!");
//        }
//        used = true;
//        SimulatorManager.getInstance().schedule(this, millisPeriod);
//    }

//    @Override
//    public void removeSimulator()
//    {
//        SimulatorManager.getInstance().remove(this);
//    }

}
