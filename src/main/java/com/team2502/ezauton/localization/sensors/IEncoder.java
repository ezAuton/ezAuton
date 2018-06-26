package com.team2502.ezauton.localization.sensors;

public interface IEncoder extends ITachometer
{
    /**
     * @return revolutions
     */
    double getPosition();
}
