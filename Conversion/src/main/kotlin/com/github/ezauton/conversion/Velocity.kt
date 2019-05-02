package com.github.ezauton.conversion

import java.lang.Math.abs

data class Velocity(override val value: Double) : Value<Velocity> {
    override fun Number.wrap() = Velocity(value)
    fun abs() = Velocity(abs(value))
}

operator fun Distance.div(other: Duration) = Velocity(this.value / other.value)
