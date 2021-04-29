//package com.github.ezauton.core.localization
//
//import com.github.ezauton.conversion.ScalarVector
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Test
//
//class ocationEstimatorTest {
//
//  @Test
//  fun `tank robot estimator interface`() {
//
//    val estimator1 = object : TankRobotVelocityEstimator {
//      override fun estimateAbsoluteVelocity(): ScalarVector {
//        return ScalarVector(0.0, -3.0)
//      }
//
//      override fun getRightTranslationalWheelVelocity(): Double {
//        return -3.0
//      }
//
//      override fun getLeftTranslationalWheelVelocity(): Double {
//        return -3.0
//      }
//    }
//
//    assertEquals(3.0, estimator1.avgTranslationalWheelSpeed)
//    assertEquals(-3.0, estimator1.avgTranslationalWheelVelocity)
//  }
//}
