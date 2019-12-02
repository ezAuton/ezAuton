package com.github.ezauton.core.localization.sensors;

/**
 * A sensor which can record revolutions/s and revolutions as a distance
 *
 * Knows all about rotations and rotational velocity
 *
 * Implicitly accounts for encoder ticks
 */
public interface RotationalDistanceSensor extends Tachometer {
    /**
     * @return revolutions
     */
    double getPosition();
}
