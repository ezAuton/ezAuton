package com.github.ezauton.conversion

interface Value<SELF : Value<SELF>> : Comparable<SELF> {

    val value: Number
    fun Number.wrap(): SELF

    operator fun plus(other: SELF) = (value.toDouble() + other.value.toDouble()).wrap()
    operator fun minus(other: SELF) = (value.toDouble() - other.value.toDouble()).wrap()
    operator fun times(scalar: Int) = (value.toDouble() * scalar).wrap()
    operator fun times(scalar: Double) = (value.toDouble() * scalar).wrap()
    operator fun div(other: SELF) = value.toDouble() / other.value.toDouble()
    operator fun rangeTo(other: SELF): ClosedFloatingPointRange<SELF> {
        return object : ClosedFloatingPointRange<SELF> {
            override val endInclusive get() = other
            override val start get() = value.wrap()
            override fun lessThanOrEquals(a: SELF, b: SELF) = a.value.toDouble() <= b.value.toDouble()
        }
    }

    override operator fun compareTo(other: SELF) = value.toDouble().compareTo(other.value.toDouble())

    fun convert(other: SELF) = value.toDouble() / other.value.toDouble()
}
