//package com.github.ezauton.core.math
//
//import com.github.ezauton.conversion.*
//import com.github.ezauton.core.utils.math.*
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Assertions.assertTrue
//import org.junit.jupiter.api.Test
//import kotlin.math.sqrt
//
//class KinematicsTest {
//  @Test
//  fun testNavXBound() {
//    assertEquals(355.0, navXBound(-5.0), 0.001)
//    assertEquals(14.0, navXBound(14.0), 0.001)
//  }
//
//  @Test // fail
//  fun testAbsoluteDPos45() {
//    val dPos = getAbsoluteDPosLine(1.0, 1.0, 1.0, (Math.PI / 4f).radians)
//
//    assertEquals(-sqrt((1 / 2f).toDouble()), dPos[0], 0.001)
//    assertEquals(sqrt((1 / 2f).toDouble()), dPos[1], 0.001)
//  }
//
//  @Test
//  fun navXToRad() {
//    // TODO: returns cw radians not ccw I think
//    var rad = navXToRad(270.0)
//
//    assertEquals(Math.PI / 2f, rad, 0.001)
//
//    rad = navXToRad((270 + 360).toDouble())
//    assertEquals(Math.PI / 2f, rad, 0.001)
//
//    rad = navXToRad((270 - 360).toDouble())
//    assertEquals(Math.PI / 2f, rad, 0.001)
//  }
//
//  /**
//   * Should be a complete rotation around a circle (dpos = 0)
//   */
//  @Test
//  fun arcDposArcStraight0Heading() {
//    // l * pi = 1 (circumference)
//    // 1/pi = l
//    val absoluteDPosCurve = getAbsoluteDPosCurve(1.0, 1.0, 123.0, zero())
//    assertEquals(0.0, absoluteDPosCurve[0], 1.0)
//    assertEquals(0.0, absoluteDPosCurve[1], 1.0)
//  }
//
//  @Test
//  fun arcDposArcStraight45Heading() {
//    // l * pi = 1 (circumference)
//    // 1/pi = l
//    val absoluteDPosCurve = getAbsoluteDPosCurve(1.0, 1.0, 123.0, (Math.PI / 4f).radians)
//    assertEquals(-sqrt((1 / 2f).toDouble()), absoluteDPosCurve[0], 0.001)
//    assertEquals(sqrt((1 / 2f).toDouble()), absoluteDPosCurve[1], 0.001)
//  }
//
//  @Test
//  fun ardDPosNaNHandling() {
//    val dPos = getAbsoluteDPosCurve(-0.010000000000001563, -0.010000000000001563, 30 / 12.0, 0.4509467)
//    assertTrue(dPos.isFinite)
//  }
//
//  @Test
//  fun testAbsoluteToRelativeCoord() {
//    val robotPos: ConcreteVector<Distance> = vec(4.0, 4.0)
//
//    // We will convert this to absolute coords
//    var targetPos = robotPos
//
//    assertEquals(origin<Distance>(2), absoluteToRelativeCoord(targetPos, robotPos, 0.0.degrees))
//    assertEquals(origin<Distance>(2), absoluteToRelativeCoord(targetPos, robotPos, 35.0.degrees))
//    assertEquals(origin<Distance>(2), absoluteToRelativeCoord(targetPos, robotPos, (-180.0).degrees))
//
//    targetPos = vec(5.0, 5.0)
//
//    // (1, 1)
//    vectorsCloseEnough(vec(1, 1), absoluteToRelativeCoord(targetPos, robotPos, 0.0.radians))
//    vectorsCloseEnough(vec(-1, -1), absoluteToRelativeCoord(targetPos, robotPos, -Math.PI.radians))
//    vectorsCloseEnough(vec(ROOT_2, 0.0), absoluteToRelativeCoord(targetPos, robotPos, (Math.PI / 4).radians))
//  }
//
//  @Test
//  fun testGetSpeedVector() {
//    val i = ScalarVector(1, 0)
//    val j = ScalarVector(0, 1)
//
//    vectorsCloseEnough(j, getVector(1.0, 0.0))
//    vectorsCloseEnough(i, getVector(1.0, -Math.PI / 2))
//    vectorsCloseEnough(i.add(j), MathUtils.Geometry.getVector(ROOT_2, -Math.PI / 4))
//  }
//
//  @Test
//  fun testGetPos() {
//    val standstill = getPos(10.0, 0.0, 0.0, 100.0)
//    assertEquals(10.0, standstill, 1E-6)
//
//    val noAccel = getPos(10.0, 10.0, 0.0, 100.0) // 10 + 10*100
//    assertEquals((10 + 10 * 100).toDouble(), noAccel, 1E-6)
//
//    val accel = getPos(0.0, 0.0, 1.0, 2.0) // 1/2*1*2^2
//    assertEquals((1 / 2f * 1f * 2f * 2f).toDouble(), accel, 1E-6)
//  }
//
//  @Test
//  fun testAngularVelocity() {
//    // straight
//    assertEquals(0.0, getAngularDistance(1.0, 1.0, 1.0), 1E-6)
//    assertEquals(0.0, getAngularDistance(0.0, 0.0, 1.0), 1E-6)
//
//    assertTrue(getAngularDistance(0.0, 1.0, 1.0) > 0)
//    assertTrue(getAngularDistance(1.0, 0.0, 1.0) < 0)
//  }
//
//  @Test
//  fun testTrajRadius() {
//    assertTrue(getTrajectoryRadius(0.0, 1.0, 1.0) > 0)
//    assertTrue(getTrajectoryRadius(1.0, 0.0, 1.0) < 0)
//  }
//
//  @Test
//  fun testRelativeDPosCurve() {
//    // straight
//    vectorsCloseEnough(svec(0.0, 1.0), getRelativeDPosCurve(1.0, 1.0, 1.0))
//
//    // full circle
//    vectorsCloseEnough(svec(0.0, 0.0), getRelativeDPosCurve(Math.PI, 0.0, (1 / 2f).toDouble()))
//
//    vectorsCloseEnough(svec(0.5, 0.0), getRelativeDPosCurve(Math.PI / 2, 0.0, (1 / 2f).toDouble()))
//  }
//
//  private fun vectorsCloseEnough(a: ConcreteVector<*>, b: ConcreteVector<*>) {
//    assertEquals(a[0].value, b[0].value, 1E-3)
//    assertEquals(a[1].value, b[1].value, 1E-3)
//  }
//
//  private fun vectorsCloseEnough(a: ScalarVector, b: ScalarVector) {
//    assertEquals(a[0], b[0], 1E-3)
//    assertEquals(a[1], b[1], 1E-3)
//  }
//}
