package com.github.ezauton.core.localization.sensors;

import com.github.ezauton.core.utils.Stopwatch;

import java.util.concurrent.TimeUnit;

/**
 * A utility class used for encoder conversion
 */
public class Encoders
{
    public static IEncoder fromTachometer(ITachometer tachometer, Stopwatch stopwatch)
    {
        return new IEncoder()
        {

            double position = 0;

            @Override
            public double getVelocity()
            {
                return tachometer.getVelocity();
            }

            @Override
            public double getPosition()
            {
                position += stopwatch.pop(TimeUnit.SECONDS) * tachometer.getVelocity();
                return position;
            }
        };
    }
}
