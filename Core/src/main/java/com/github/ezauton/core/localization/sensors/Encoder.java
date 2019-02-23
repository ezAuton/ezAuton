package com.github.ezauton.core.localization.sensors;

/**
 * A sensor which can record revolutions/s and revolutions as a distance
 */
public interface Encoder extends Tachometer {
    /**
     * @return revolutions
     */
    double getPosition();
}
