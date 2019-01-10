package com.github.ezauton.core.test.kotlin.util

import com.github.ezauton.core.utils.InterpolationMap
import org.junit.Assert
import org.junit.Test

class UtilsTest {

    @Test
    fun `test interpolator get`() {

        val map = mapOf(
                1.0 to 2.0,
                2.0 to 1.0,
                4.0 to 3.0
        )


        val interpMap = InterpolationMap(map)
        interpMap.apply {
            Assert.assertEquals(2.0, interpMap[0.0], 1E-6)
            Assert.assertEquals(2.0, interpMap[1.0], 1E-6)
            Assert.assertEquals(1.5, interpMap[1.5], 1E-6)
            Assert.assertEquals(1.0, interpMap[2.0], 1E-6)
            Assert.assertEquals(2.0, interpMap[3.0], 1E-6)
            Assert.assertEquals(3.0, interpMap[4.0], 1E-6)
            Assert.assertEquals(3.0, interpMap[5.0], 1E-6)
        }
    }

    @Test
    fun `test interpolator integrate`() {

        val map = mapOf(
                1.0 to 2.0,
                2.0 to 1.0,
                4.0 to 3.0
        )


        val interpMap = InterpolationMap(map)
        val integrate1 = interpMap.integrate(0.0, 5.0)

        interpMap.apply {
            Assert.assertEquals(2.0, integrate(0.0, 1.0), 1E-6)
            Assert.assertEquals(1.5, integrate(1.0, 2.0), 1E-6)
            Assert.assertEquals(2 * 2.0, integrate(2.0, 4.0), 1E-6)
            Assert.assertEquals(3.0, integrate(4.0, 5.0), 1E-6)
        }

        // [0,1] -> 2
        // [1,2] -> 1.5
        // [2,4] -> 2*2
        // [4,5] -> 3

//        Assert.assertEquals()

//        Assert.assertEquals(2 + (0.5+1) + 2*2 + 3, subIntegrate, 1E-6)
        Assert.assertEquals(2 + (0.5 + 1) + 2 * 2 + 3, integrate1, 1E-6)
    }

    @Test
    fun `test simple integral`() {
        val map = mapOf(
                0.0 to 1.0,
                1.0 to 1.0
        )
        val interpMap = InterpolationMap(map)
        val integrate = interpMap.integrate(0.0, 1.0)
        Assert.assertEquals(1.0, integrate, 1E-6)

    }
}