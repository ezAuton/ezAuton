package com.team2502.ezauton.localization.sensors;

public interface ITachometer extends ISensor {
    /**
     * @return revolutions / s
     */
    double getVelocity();
}
