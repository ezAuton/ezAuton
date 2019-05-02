package com.github.ezauton.conversion

interface Value<SELF : Value<SELF>> {


    val value: Number
    fun Number.wrap(): SELF

    operator fun plus(other: SELF) = (value.toDouble() + other.value.toDouble()).wrap()
    operator fun minus(other: SELF) = (value.toDouble() - other.value.toDouble()).wrap()
    operator fun times(scalar: Int) = (value.toDouble() * scalar).wrap()
    operator fun times(scalar: Double) = (value.toDouble() * scalar).wrap()
    operator fun rangeTo(other: SELF): ClosedFloatingPointRange<Double> {
        val from = value.toDouble()
        val to = other.value.toDouble()
        return from..to
    }

    operator fun compareTo(other: SELF) = value.toDouble().compareTo(other.value.toDouble())

    fun convert(other: SELF) = value.toDouble() / other.value.toDouble()

}
