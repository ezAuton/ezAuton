package com.team2502.ezpp.localization.sensors;

public interface IEncoder extends ITachometer {
    /**
     * @return revolutions
     */
    float getPosition();
}
