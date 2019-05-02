package com.github.ezauton.conversion

class Work(override val value: Double) : Value<Work> {
    override fun Number.wrap() = Work(toDouble())
}

val Number.joules get() = Work(toDouble())
