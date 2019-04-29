package com.github.ezauton.core.localization.sensors

/**
 * Like an encoder but for translational distance instead of rotations. An example of a TranslationalDistanceSensor is an [EncoderWheel].
 */
interface TranslationalDistanceSensor {
    /**
     * Get the translational position
     *
     * @return
     */
    val position: Double

    /**
     * Get the **translational** velocity (should be VELOCITY so positive *and* negative)
     *
     * @return
     */
    val velocity: Double
}
