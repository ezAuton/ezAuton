package com.team2502.ezpp.localization.sensors;

public interface ITachometer extends ISensor {
    /**
     * @return revolutions / s
     */
    float getVelocity();
}
