package com.github.ezauton.core.test.kotlin.localization

import com.github.ezauton.core.localization.sensors.Encoders
import com.github.ezauton.core.utils.ManualClock
import com.github.ezauton.core.utils.Stopwatch
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class SensorTest {

//    @Test
//    fun `tachometer to encoder test`(){
//        val clock = ManualClock()
//        val stopwatch = Stopwatch(clock)
//
//        val encoder = Encoders.fromTachometer({ 100.0 }, stopwatch)
//
//        assertEquals(0, encoder.position)
//
//        clock.addTime(1, TimeUnit.SECONDS)
//
//        assertEquals(2,encoder.position)
//
//        clock.addTime(3, TimeUnit.SECONDS)
//        assertEquals(6,encoder.position)
//    }
}