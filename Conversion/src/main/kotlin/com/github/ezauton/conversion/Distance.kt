package com.github.ezauton.conversion

class Distance private constructor(val meters: Double) : Value<Distance> {
    override fun Number.wrap() = fromDoubleMeters(this.toDouble())

    override val value: Double get() = meters

    companion object {
        fun fromDoubleMeters(source: Double) = Distance(source)
        val NONE = fromDoubleMeters(0.0)
    }
}

val Double.meters get() = Distance.fromDoubleMeters(this)
val Double.feet get() = (0.3048 * this).meters
val Double.inches get() = (this / 12.0).feet
val Double.cm get() = (this / 1_000).meters
val Double.yards get() = (this * 3.0).feet

val meter = 1.0.meters
val foot = 1.0.feet
val inch = 1.0.inches
val cm = 1.0.cm
val yard = 1.0.yards
