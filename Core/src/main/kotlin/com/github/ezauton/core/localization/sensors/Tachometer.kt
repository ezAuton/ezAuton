package com.github.ezauton.core.localization.sensors

/**
 * A sensor which can measure revolutions / s (but not position)
 */
interface Tachometer : Sensor {
    /**
     * @return revolutions / s
     */
    val velocity: Double
}
