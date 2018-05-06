package com.team2502.ezpp.localization.sensors;

import com.kauailabs.navx.frc.AHRS;

public class Gyros {

    public static ICompass fromNavx(AHRS navx)
    {
        return () -> {
            float angle = (float) -navx.getAngle(); // we want CCW orientation
            float boundedAngle = angle % 360;
            if(boundedAngle < 0)
            {
                boundedAngle = 360+boundedAngle;
            }
            return boundedAngle;
        };
    }


}
