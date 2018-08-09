package com.team2502.ezauton.wpilib.command;

import com.team2502.ezauton.localization.sensors.ICompass;

public class Gyros
{
    public static ICompass fromNavx(AHRS navx)
    {
        return () -> {
            double angle = -navx.getAngle(); // we want CCW orientation
            double boundedAngle = angle % 360;
            if(boundedAngle < 0)
            {
                boundedAngle = 360 + boundedAngle;
            }
            return boundedAngle;
        };
    }
}
