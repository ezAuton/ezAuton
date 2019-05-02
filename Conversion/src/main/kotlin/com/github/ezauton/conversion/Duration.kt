package com.github.ezauton.conversion

class Duration private constructor(private val millis: Long): Value<Duration> {
    override fun Number.wrap(): Duration = fromLongMillis(this.toLong())

    override val value get() = millis

    companion object {
        fun fromLongMillis(source: Long) = Duration(source)
        val NONE = fromLongMillis(0)
    }

    fun plusMillis(toAdd: Long) = Duration(millis + toAdd)
}

fun now(): Duration {
    return Duration.fromLongMillis(System.currentTimeMillis())
}

val Long.millis: Duration get() = Duration.fromLongMillis(this)
val Long.seconds: Duration get() = (this * 1000).millis
val Long.minutes get() = (this * 60).seconds
val Long.hours get() = (this * 60).minutes

val millis = 1.millis
val second = 1.seconds
val minute  = 1.minutes
val hour = 1.hours

val Int.millis: Duration get() = Duration.fromLongMillis(this.toLong())
val Int.seconds: Duration get() = (this * 1000).millis
val Int.minutes get() = (this * 60).seconds
val Int.hours get() = (this * 60).minutes

val Double.seconds: Duration get() = (this * 1000).toInt().millis
val Double.minutes get() = (this * 60).seconds
val Double.hours get() = (this * 60).minutes

