package com.github.ezauton.core.localization.sensors;

/**
 * A sensor which can record revolutions/s and revolutions as a distance
 */
public interface RotationalDistanceSensor extends Tachometer {
    /**
     * @return revolutions
     */
    double getPosition();
}
