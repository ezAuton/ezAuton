//package com.github.ezauton.core.util
//
//import com.github.ezauton.core.actuators.Actuators
//import com.github.ezauton.core.actuators.VelocityMotor
//import com.github.ezauton.core.actuators.VoltageMotor
//import com.github.ezauton.core.actuators.implementations.BaseSimulatedMotor
//import com.github.ezauton.core.actuators.implementations.BoundedVelocityProcessor
//import com.github.ezauton.core.actuators.implementations.RampUpVelocityProcessor
//import com.google.common.util.concurrent.AtomicDouble
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.Test
//import java.util.concurrent.TimeUnit
//
//class ActuatorsTest {
//  @Test
//  fun testSimpleVoltToVel() {
//    val atomicDouble = AtomicDouble()
//    val voltageMotor = VoltageMotor { atomicDouble.set(it) }
//    val velocityMotor = Actuators.roughConvertVoltageToVel(voltageMotor, 16.0)
//
//    velocityMotor.runVelocity(16.0)
//    assertEquals(1.0, atomicDouble.toDouble(), 1E-6)
//
//    velocityMotor.runVelocity(20.0)
//    assertEquals(1.0, atomicDouble.toDouble(), 1E-6)
//
//    velocityMotor.runVelocity(8.0)
//    assertEquals(0.5, atomicDouble.toDouble(), 1E-6)
//
//    velocityMotor.runVelocity(0.0)
//    assertEquals(0.0, atomicDouble.toDouble(), 1E-6)
//
//    velocityMotor.runVelocity(-8.0)
//    assertEquals(-0.5, atomicDouble.toDouble(), 1E-6)
//
//    velocityMotor.runVelocity(-16.0)
//    assertEquals(-1.0, atomicDouble.toDouble(), 1E-6)
//
//    velocityMotor.runVelocity(-20.0)
//    assertEquals(-1.0, atomicDouble.toDouble(), 1E-6)
//  }
//
//  @Test
//  fun testRampUpVelocityProcessor() {
//    val velocity = AtomicDouble()
//    val velocityMotor = VelocityMotor { velocity.set(it) }
//
//    val clock = ManualClock()
//
//    val velocityProcessor = RampUpVelocityProcessor(velocityMotor, clock, 1.0)
//
//    velocityProcessor.runVelocity(2.0)
//    clock.addTime(1, TimeUnit.SECONDS)
//    velocityProcessor.update()
//
//    assertEquals(1.0, velocity.toDouble(), 1E-6)
//
//    clock.addTime(1, TimeUnit.SECONDS)
//    velocityProcessor.update()
//
//    assertEquals(2.0, velocity.toDouble(), 1E-6)
//
//    clock.addTime(1, TimeUnit.SECONDS)
//    velocityProcessor.update()
//
//    assertEquals(2.0, velocity.toDouble(), 1E-6)
//
//    velocityProcessor.runVelocity(1.0)
//    clock.addTime(990, TimeUnit.MILLISECONDS)
//    velocityProcessor.update()
//    assertEquals(1.01, velocity.toDouble(), 1E-6)
//
//    velocityProcessor.runVelocity(10.0)
//    clock.addTime(990, TimeUnit.MILLISECONDS)
//    velocityProcessor.update()
//    assertEquals(2.0, velocity.toDouble(), 1E-6)
//    assertEquals(2.0, velocityProcessor.lastVelocity, 1E-6)
//  }
//
//  @Test
//  fun testBoundedVelocityProcessor() {
//    val velocity = AtomicDouble()
//    val velocityMotor = VelocityMotor { velocity.set(it) }
//
//    val velocityProcessor = BoundedVelocityProcessor(velocityMotor, 16.0)
//
//    velocityProcessor.runVelocity(1.0)
//    assertEquals(1.0, velocity.toDouble(), 1E-6)
//
//    velocityProcessor.runVelocity(-15.0)
//    assertEquals(-15.0, velocity.toDouble(), 1E-6)
//
//    velocityProcessor.runVelocity(-16.0)
//    assertEquals(-16.0, velocity.toDouble(), 1E-6)
//
//    velocityProcessor.runVelocity(16.0)
//    assertEquals(16.0, velocity.toDouble(), 1E-6)
//
//    velocityProcessor.runVelocity(-17.0)
//    assertEquals(-16.0, velocity.toDouble(), 1E-6)
//
//    velocityProcessor.runVelocity(18.0)
//    assertEquals(16.0, velocity.toDouble(), 1E-6)
//  }
//
//  @Test
//  fun testBoundedVelocityProcessorNegMaxSpeed() {
//    val velocity = AtomicDouble(0.0)
//
//    assertThrows(IllegalArgumentException::class.java) { BoundedVelocityProcessor(VelocityMotor { velocity.set(it) }, -10.0) }
//  }
//
//  @Test
//  fun testBaseSimulatedMotor() {
//    val velocity = AtomicDouble()
//    val velocityMotor = VelocityMotor { velocity.set(it) }
//
//    val clock = ManualClock()
//    val simulatedMotor = BaseSimulatedMotor(clock)
//
//    simulatedMotor.runVelocity(1.0)
//    clock.addTime(1, TimeUnit.SECONDS)
//
//    assertEquals(1.0, simulatedMotor.position, 1E-6)
//    assertEquals(1.0, simulatedMotor.velocity, 1E-6)
//
//    clock.addTime(1, TimeUnit.SECONDS)
//
//    assertEquals(2.0, simulatedMotor.position, 1E-6)
//    assertEquals(1.0, simulatedMotor.velocity, 1E-6)
//
//    simulatedMotor.runVelocity(2.0)
//
//    clock.addTime(1, TimeUnit.SECONDS)
//
//    assertEquals(4.0, simulatedMotor.position, 1E-6)
//
//    simulatedMotor.runVelocity(3.0)
//
//    clock.addTime(1, TimeUnit.SECONDS)
//
//    assertEquals(7.0, simulatedMotor.position, 1E-6)
//
//    simulatedMotor.subscribed = velocityMotor
//    simulatedMotor.runVelocity(2.0)
//
//    assertEquals(2.0, velocity.toDouble(), 1E-6)
//    assertSame(velocityMotor, simulatedMotor.subscribed)
//  }
//}
