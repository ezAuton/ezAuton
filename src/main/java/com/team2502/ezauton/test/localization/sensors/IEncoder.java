package com.team2502.ezauton.test.localization.sensors;

public interface IEncoder extends ITachometer {
    /**
     * @return revolutions
     */
    double getPosition();
}
