package com.team2502.ezauton.utils;

import java.util.Timer;
import java.util.TimerTask;

public class RealClock implements IClock
{
    public static final RealClock CLOCK = new RealClock();

    private RealClock() {}

    @Override
    public long getTime()
    {
        return System.currentTimeMillis();
    }

    @Override
    public void scheduleAt(long millis, Runnable runnable)
    {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run()
            {
                runnable.run();
            }
        }, millis - getTime());
    }
}
