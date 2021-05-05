package com.github.ezauton.core.simulator

import com.github.ezauton.conversion.radians
import com.github.ezauton.conversion.sec
import com.github.ezauton.conversion.seconds
import com.github.ezauton.core.action.action
import com.github.ezauton.core.action.delay
import com.github.ezauton.core.action.runWithTimeout
import com.github.ezauton.core.actuators.implementations.BaseSimulatedMotor
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeoutException

class SimulatedMotorTest {
  @Test
  @Throws(TimeoutException::class)
  fun testMotor() = runBlocking {


    val action = action {

      val motor = BaseSimulatedMotor()

      assertEquals(0.0, motor.position.value, 0.1)
      motor.runVelocity(1.0.radians / sec)

      delay(1.seconds)

      assertEquals(1.0, motor.position.value, 0.1)

      delay(1.seconds)

      assertEquals(2.0, motor.position.value, 0.1)
      motor.runVelocity(10.0.radians / sec)
      assertEquals(2.0, motor.position.value, 0.1)

      delay(1.seconds)

      assertEquals(12.0, motor.position.value, 0.1)
    }

    action.runWithTimeout(5.seconds)
  }
}
