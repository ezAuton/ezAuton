package com.team2502.ezauton.test.localization.sensors;

public interface ITachometer extends ISensor {
    /**
     * @return revolutions / s
     */
    double getVelocity();
}
