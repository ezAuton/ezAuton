package com.github.ezauton.core.utils;

import java.util.concurrent.TimeUnit;

/**
 * A clock based off of {@link RealClock} but is warped
 */
public class TimeWarpedClock implements IClock {
    private final double speedMultiplier;
    private final RealClock realClock;
    private long startTime;
    private long timeStartedAt;

    public TimeWarpedClock(double speedMultiplier, long startTime) {
        realClock = RealClock.CLOCK;
        this.speedMultiplier = speedMultiplier;
        this.startTime = startTime;
        timeStartedAt = System.currentTimeMillis();
    }

    public TimeWarpedClock(double speedMultiplier) {
        this(speedMultiplier, System.currentTimeMillis());
    }

    public double getSpeed() {
        return speedMultiplier;
    }

    public long getStartTime() {
        return startTime;
    }

    @Override
    public long getTime() {
        long realDt = realClock.getTime() - timeStartedAt;
        return (long) (realDt * speedMultiplier + startTime);
    }

    @Override
    public void scheduleAt(long millis, Runnable runnable) {
        double realDt = (millis - getTime()) / speedMultiplier;
        realClock.scheduleIn((long) realDt, TimeUnit.MILLISECONDS, runnable);
    }

    @Override
    public void sleep(long dt, TimeUnit timeUnit) throws InterruptedException {
        Thread.sleep((long) (timeUnit.toMillis(dt) / speedMultiplier));
    }
}
