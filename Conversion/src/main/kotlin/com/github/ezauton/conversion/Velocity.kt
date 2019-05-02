package com.github.ezauton.conversion

data class Velocity(override val value: Double) : Value<Velocity> {
    override fun Number.wrap() = Velocity(value)
}

operator fun Distance.div(other: Duration) = Velocity(this.value / other.value)
