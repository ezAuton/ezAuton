package com.team2502.ezauton.localization.sensors;

import com.kauailabs.navx.frc.AHRS;

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
