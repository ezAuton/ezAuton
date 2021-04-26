package com.github.ezauton.core.math

import com.github.ezauton.conversion.ScalarVector
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class KinematicsTest {
  @Test
  fun testNavXBound() {
    assertEquals(355.0, MathUtils.Kinematics.navXBound(-5.0), 0.001)
    assertEquals(14.0, MathUtils.Kinematics.navXBound(14.0), 0.001)
  }

  @Test // fail
  fun testAbsoluteDPos45() {
    val dPos = MathUtils.Kinematics.getAbsoluteDPosLine(1.0, 1.0, 1.0, Math.PI / 4f)

    assertEquals(-Math.sqrt((1 / 2f).toDouble()), dPos.get(0), 0.001)
    assertEquals(Math.sqrt((1 / 2f).toDouble()), dPos.get(1), 0.001)
  }

  @Test
  fun navXToRad() {
    // TODO: returns cw radians not ccw I think
    var rad = MathUtils.Kinematics.navXToRad(270.0)

    assertEquals(Math.PI / 2f, rad, 0.001)

    rad = MathUtils.Kinematics.navXToRad((270 + 360).toDouble())
    assertEquals(Math.PI / 2f, rad, 0.001)

    rad = MathUtils.Kinematics.navXToRad((270 - 360).toDouble())
    assertEquals(Math.PI / 2f, rad, 0.001)
  }

  /**
   * Should be a complete rotation around a circle (dpos = 0)
   */
  @Test
  fun arcDposArcStraight0Heading() {
    // l * pi = 1 (circumference)
    // 1/pi = l
    val absoluteDPosCurve = MathUtils.Kinematics.getAbsoluteDPosCurve(1.0, 1.0, 123.0, 0.0)
    assertEquals(0.0, absoluteDPosCurve.get(0), 1.0)
    assertEquals(0.0, absoluteDPosCurve.get(1), 1.0)
  }

  @Test
  fun arcDposArcStraight45Heading() {
    // l * pi = 1 (circumference)
    // 1/pi = l
    val absoluteDPosCurve = MathUtils.Kinematics.getAbsoluteDPosCurve(1.0, 1.0, 123.0, Math.PI / 4f)
    assertEquals(-Math.sqrt((1 / 2f).toDouble()), absoluteDPosCurve.get(0), 0.001)
    assertEquals(Math.sqrt((1 / 2f).toDouble()), absoluteDPosCurve.get(1), 0.001)
  }

  @Test
  fun ardDPosNaNHandling() {
    val dPos = MathUtils.Kinematics.getAbsoluteDPosCurve(-0.010000000000001563, -0.010000000000001563, 30 / 12.0, 0.4509467)
    assertTrue(dPos.isFinite)
  }

  @Test
  fun testAbsoluteToRelativeCoord() {
    val robotPos = ScalarVector(4, 4)

    // We will convert this to absolute coords
    var targetPos = robotPos

    assertEquals(ScalarVector(0, 0), MathUtils.LinearAlgebra.absoluteToRelativeCoord(targetPos, robotPos, 0.0))
    assertEquals(ScalarVector(0, 0), MathUtils.LinearAlgebra.absoluteToRelativeCoord(targetPos, robotPos, 35.0))
    assertEquals(ScalarVector(0, 0), MathUtils.LinearAlgebra.absoluteToRelativeCoord(targetPos, robotPos, -180.0))

    targetPos = ScalarVector(5, 5)

    // (1, 1)
    vectorsCloseEnough(ScalarVector(1, 1), MathUtils.LinearAlgebra.absoluteToRelativeCoord(targetPos, robotPos, 0.0))
    vectorsCloseEnough(ScalarVector(-1, -1), MathUtils.LinearAlgebra.absoluteToRelativeCoord(targetPos, robotPos, -Math.PI))
    vectorsCloseEnough(ScalarVector(ROOT_2, 0), MathUtils.LinearAlgebra.absoluteToRelativeCoord(targetPos, robotPos, Math.PI / 4))
  }

  @Test
  fun testGetSpeedVector() {
    val i = ScalarVector(1, 0)
    val j = ScalarVector(0, 1)

    vectorsCloseEnough(j, MathUtils.Geometry.getVector(1.0, 0.0))
    vectorsCloseEnough(i, MathUtils.Geometry.getVector(1.0, -Math.PI / 2))
    vectorsCloseEnough(i.add(j), MathUtils.Geometry.getVector(ROOT_2, -Math.PI / 4))
  }

  @Test
  fun testGetPos() {
    val standstill = MathUtils.Kinematics.getPos(10.0, 0.0, 0.0, 100.0)
    assertEquals(10.0, standstill, 1E-6)

    val noAccel = MathUtils.Kinematics.getPos(10.0, 10.0, 0.0, 100.0) // 10 + 10*100
    assertEquals((10 + 10 * 100).toDouble(), noAccel, 1E-6)

    val accel = MathUtils.Kinematics.getPos(0.0, 0.0, 1.0, 2.0) // 1/2*1*2^2
    assertEquals((1 / 2f * 1f * 2f * 2f).toDouble(), accel, 1E-6)
  }

  @Test
  fun testAngularVelocity() {
    // straight
    assertEquals(0.0, MathUtils.Kinematics.getAngularDistance(1.0, 1.0, 1.0), 1E-6)
    assertEquals(0.0, MathUtils.Kinematics.getAngularDistance(0.0, 0.0, 1.0), 1E-6)

    assertTrue(MathUtils.Kinematics.getAngularDistance(0.0, 1.0, 1.0) > 0)
    assertTrue(MathUtils.Kinematics.getAngularDistance(1.0, 0.0, 1.0) < 0)
  }

  @Test
  fun testTrajRadius() {
    assertTrue(MathUtils.Kinematics.getTrajectoryRadius(0.0, 1.0, 1.0) > 0)
    assertTrue(MathUtils.Kinematics.getTrajectoryRadius(1.0, 0.0, 1.0) < 0)
  }

  @Test
  fun testRelativeDPosCurve() {
    // straight
    vectorsCloseEnough(ScalarVector(0, 1), MathUtils.Kinematics.getRelativeDPosCurve(1.0, 1.0, 1.0))

    // full circle
    vectorsCloseEnough(ScalarVector(0, 0), MathUtils.Kinematics.getRelativeDPosCurve(Math.PI, 0.0, (1 / 2f).toDouble()))

    vectorsCloseEnough(ScalarVector(0.5, 0), MathUtils.Kinematics.getRelativeDPosCurve(Math.PI / 2, 0.0, (1 / 2f).toDouble()))
  }

  private fun vectorsCloseEnough(a: ScalarVector, b: ScalarVector) {
    assertEquals(a.get(0), b.get(0), 1E-3)
    assertEquals(a.get(1), b.get(1), 1E-3)
  }
}
