package com.github.ezauton.core.localization.estimators;

import com.github.ezauton.core.localization.IRotationalLocationEstimator;
import com.github.ezauton.core.localization.sensors.ICompass;

public class CompassRotationalLocationEstimator implements IRotationalLocationEstimator
{
    private final ICompass compass;

    public CompassRotationalLocationEstimator(ICompass compass) {this.compass = compass;}


    @Override
    public double estimateHeading()
    {
        return compass.getRadians();
    }
}
