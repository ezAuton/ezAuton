package org.github.ezauton.ezauton.localization.sensors;

/**
 * A sensor which can record revolutions/s and revolutions as a distance
 */
public interface IEncoder extends ITachometer
{
    /**
     * @return revolutions
     */
    double getPosition();
}
