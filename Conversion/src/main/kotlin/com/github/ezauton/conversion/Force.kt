package com.github.ezauton.conversion

class Force(override val value: Double) : Value<Force> {
    override fun Number.wrap(): Force = Force(toDouble())
    operator fun times(other: Distance) = Work(value + other.meters)
}

val Number.newtons get() = Force(toDouble())
