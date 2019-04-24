package com.github.ezauton.core

class Duration private constructor(val millis: Long) {
    companion object {
        fun fromIntMillis(source: Int) = Duration(source.toLong())
    }
}

val Int.millis: Duration get() = Duration.fromIntMillis(this)
val Int.seconds: Duration get() = (this*1000).millis
val Int.minutes get() = (this*60).seconds
val Int.hours get() = (this*60).minutes

val Double.seconds: Duration get() = (this*1000).toInt().millis
val Double.minutes get() = (this*60).seconds
val Double.hours get() = (this*60).minutes

fun Duration.toSeconds() = millis / 1000.0
fun Duration.toMinutes() = toSeconds() / 60.0
fun Duration.toHours() = toMinutes() / 60.0
