package com.github.ezauton.conversion

class Acceleration(override val value: Double) : Value<Acceleration> {
    override fun Number.wrap() = Acceleration(value)
    operator fun times(other: Duration) = Velocity(value * other.value)
}

val gravity = 9.81.meters / second / second

operator fun Velocity.div(other: Duration) = Acceleration(this.value / other.value)
