package com.github.ezauton.conversion

class Mass(override val value: Double) : Value<Mass> {
    override fun Number.wrap() = Mass((toDouble()))
    operator fun times(other: Acceleration) = Force(value * other.value)
}

val Number.kg get() = Mass(toDouble())
val Number.g get() = (toDouble() / 1_000).kg

