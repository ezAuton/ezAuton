package com.github.ezauton.core.test.kotlin.localization

import com.github.ezauton.core.localization.ITankRobotVelocityEstimator
import com.github.ezauton.core.trajectory.geometry.ImmutableVector
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LocationEstimatorTest {

    @Test
    fun `tank robot estimator interface`() {

        val estimator1 = object : ITankRobotVelocityEstimator {
            override fun estimateAbsoluteVelocity(): ImmutableVector {
                return ImmutableVector(0.0, -3.0)
            }

            override fun getRightTranslationalWheelVelocity(): Double {
                return -3.0
            }

            override fun getLeftTranslationalWheelVelocity(): Double {
                return -3.0
            }

        }

        assertEquals(3.0, estimator1.avgTranslationalWheelSpeed)
        assertEquals(-3.0, estimator1.avgTranslationalWheelVelocity)
    }
}
