package com.github.ezauton.core

class Duration private constructor(val millis: Long) {

    companion object {
        fun fromLongMillis(source: Long) = Duration(source)
        val NONE = fromLongMillis(0)
    }

    fun plusMillis(toAdd: Long) = Duration(millis + toAdd)

    operator fun plus(other: Duration) = fromLongMillis(millis + other.millis)
    operator fun minus(other: Duration) = fromLongMillis(millis - other.millis)
    operator fun times(scalar: Int) = fromLongMillis(millis * scalar)
    operator fun times(scalar: Double) = fromLongMillis((millis * scalar).toLong())
    operator fun compareTo(other: Duration) = millis.compareTo(other.millis)
}

fun now(): Duration {
    return Duration.fromLongMillis(System.currentTimeMillis())
}

val Long.millis: Duration get() = Duration.fromLongMillis(this)
val Long.seconds: Duration get() = (this * 1000).millis
val Long.minutes get() = (this * 60).seconds
val Long.hours get() = (this * 60).minutes

val Int.millis: Duration get() = Duration.fromLongMillis(this.toLong())
val Int.seconds: Duration get() = (this * 1000).millis
val Int.minutes get() = (this * 60).seconds
val Int.hours get() = (this * 60).minutes

val Double.seconds: Duration get() = (this * 1000).toInt().millis
val Double.minutes get() = (this * 60).seconds
val Double.hours get() = (this * 60).minutes

fun Duration.toSeconds() = millis / 1000.0
fun Duration.toMinutes() = toSeconds() / 60.0
fun Duration.toHours() = toMinutes() / 60.0
