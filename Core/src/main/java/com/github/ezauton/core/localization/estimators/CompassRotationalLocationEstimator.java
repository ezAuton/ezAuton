package com.github.ezauton.core.localization.estimators;

import com.github.ezauton.core.localization.RotationalLocationEstimator;
import com.github.ezauton.core.localization.sensors.Compass;

public class CompassRotationalLocationEstimator implements RotationalLocationEstimator {
    private final Compass compass;

    public CompassRotationalLocationEstimator(Compass compass) {
        this.compass = compass;
    }


    @Override
    public double estimateHeading() {
        return compass.getRadians();
    }
}
