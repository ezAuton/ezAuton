package com.github.ezauton.core.utils.math

import com.github.ezauton.conversion.*
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin


/**
 * @param initDegrees  init degrees navX (clockwise)
 * @param finalDegrees final degrees navX (counterclockwise)
 * @return the difference in radians between the two degrees from [0,2pi). Increases counterclockwise.
 */
fun getDThetaNavX(initDegrees: Double, finalDegrees: Double): Double {
  val degDif = -(finalDegrees - initDegrees)
  val radians = deg2Rad(degDif)
  val radBounded = radians % TAU
  return if (radBounded < 0) {
    TAU + radBounded
  } else radBounded
}

/**
 * Return the difference between 2 angles (in degrees)
 *
 * @param angle1 Angle 1 (in degrees)
 * @param angle2 Angle 2 (in degrees)
 * @return Difference between the angles (in degrees)
 */
fun getDAngle(angle1: Double, angle2: Double): Double {
  val simpleAngle1 = angle1 % 360
  val simpleAngle2 = angle2 % 360
  var dif = Math.abs(simpleAngle1 - simpleAngle2)
  if (dif > 180) {
    dif = 360 - dif
  }
  return dif
}

fun isCCWQuickest(angleInit: Double, angleFinal: Double): Boolean {
  var d: Double
  if (angleFinal > angleInit) {
    d = angleFinal - angleInit
    if (d > 180) {
      // Since angles are by default cw (navX) this means we should go ccw
      return true
      //                    d = 360-d;
    }
  } else if (angleInit > angleFinal) {
    d = angleInit - angleFinal
    if (d > 180) {
      d = 360 - d
    } else {
      return true
      //                    ccw = true;
    }
  }
  return false
}

/**
 * @param start The starting vector
 * @param end   The ending vector
 * @return The theta of the angle created counterclockwise between \vec{i} and the line from start->end
 * <br></br>
 * Equivalent to arctan((start - end))
 */
fun getThetaFromPoints(start: ScalarVector, end: ScalarVector): Double {
  val dx = end.get(0) - start.get(0)
  val dy = end.get(1) - start.get(1)
  return Math.atan2(dy, dx)
}

/**
 * @param magnitude The length of the vector
 * @param angle    The angle of the vector
 * @return A vector in <x></x>, y> form
 * @see ScalarVector
 */
fun <T: SIUnit<T>> polarVector2D(magnitude: T, angle: Angle): ConcreteVector<T> {
  return VECTOR_FORWARD.rotate2D(angle).times(magnitude)
}

private fun ScalarVector.rotate2D(value: Angle): ScalarVector {
  val radians = value.radians
  val newX = cos(radians) * x + sin(radians) * y
  val newY = sin(radians) * x + cos(radians) * y
  return scalarVec(newX, newY)
}

/**
 * Given a circle and a line, find where the circle intersects the line
 *
 * @param pointA One point on the line
 * @param pointB Another point on the line
 * @param center The center of the circle
 * @param radius The radius of the circle
 * @return All points on both the line and circle, should they exist.
 */
fun getCircleLineIntersectionPoint(pointA: ScalarVector, pointB: ScalarVector, center: ScalarVector, radius: Double): List<ScalarVector> {
  val baX = pointB[0] - pointA.get(0)
  val baY = pointB.get(1) - pointA.get(1)

  val caX = center.get(0) - pointA.get(0)
  val caY = center.get(1) - pointA.get(1)

  val a = baX * baX + baY * baY
  val bBy2 = baX * caX + baY * caY
  val c = caX * caX + caY * caY - radius * radius

  val pBy2 = bBy2 / a
  val q = c / a

  val disc = pBy2 * pBy2 - q
  if (disc < 0) {
    return emptyList()
  }
  // if disc == 0 ... dealt with later
  val tmpSqrt = Math.sqrt(disc)
  val abScalingFactor1 = tmpSqrt - pBy2

  val p1 = ScalarVector(pointA[0] - baX * abScalingFactor1, pointA[1] - baY * abScalingFactor1)
  if (disc == 0.0) {
    return listOf(p1)
  }

  val abScalingFactor2 = -pBy2 - tmpSqrt
  val p2 = ScalarVector(pointA[0] - baX * abScalingFactor2, pointA[1] - baY * abScalingFactor2)
  return listOf(p1, p2)
}


typealias ParametricFunction = (Double) -> ScalarVector


//fun ParametricFunction.arcLength(bounds: ClosedRange<Double>, delta: Double = 0.001): Double {
//  require(bounds.endInclusive > bounds.start)
//
//  var on = bounds.start
//  var last = this(on)
//  on += delta
//
//  var resultLength = 0.0
//
//  while (on <= bounds.endInclusive) {
//    this(t)
//    on += delta
//  }
//
//  while (t <= bounds.endInclusive) {
//    this(t)
//    resultLength += Math.hypot(x - lastX, y - lastY)
//    lastX = x
//    lastY = y
//    t += delta
//  }
//  return resultLength
//}


//fun getX(t: Double): Double
//
//fun getY(t: Double): Double
//
//operator fun get(t: Double): ScalarVector {
//  return ScalarVector(getX(t), getY(t))
//}

//fun arcLength(lowerBound: Double, upperBound: Double, delta: Double = DELTA): Double {
//  var resultLength = 0.0
//  var lastX = getX(lowerBound)
//  var lastY = getY(lowerBound)
//  var t = lowerBound + delta
//  while (t <= upperBound) {
//    val x = getX(t)
//    val y = getY(t)
//    resultLength += Math.hypot(x - lastX, y - lastY)
//    lastX = x
//    lastY = y
//    t += delta
//  }
//  return resultLength
//}
//
//fun getT(point: ScalarVector, lowerBound: Double, upperBound: Double): Double {
//  var t = lowerBound
//  while (t <= upperBound) {
//    if (get(t) == point)
//      return t
//    t += DELTA
//  }
//  return java.lang.Double.NaN
//}
//
//fun fromArcLength(arcLength: Double): ScalarVector {
//  return fromArcLength(0.0, arcLength, DELTA)
//}

//fun fromArcLength(lowerBound: Double, arcLength: Double, delta: Double = DELTA): ScalarVector {
//  var arcLength = arcLength
//  var lastX = getX(lowerBound)
//  var lastY = getY(lowerBound)
//  var resultT = lowerBound
//  var t = lowerBound + delta
//  while (arcLength >= 0) {
//    val x = getX(t)
//    val y = getY(t)
//    arcLength -= Math.hypot(x - lastX, y - lastY)
//    lastX = x
//    lastY = y
//    resultT = t
//    t += delta
//  }
//  return get(resultT)
//}


//class LineR2(internal val a: ScalarVector, internal val b: ScalarVector) : Integrable {
//  internal val slope: Double
//  internal val y_intercept: Double
//  internal val x_intercept: Double
//
//  internal val x1: Double
//  internal val x2: Double
//  internal val y1: Double
//  internal val y2: Double
//
//  init {
//    x1 = a.get(0)
//    x2 = b.get(0)
//    y1 = a.get(1)
//    y2 = b.get(1)
//
//    if (a.get(0) - b.get(0) != 0.0) {
//      slope = (a.get(1) - b.get(1)) / (a.get(0) - b.get(0))
//      y_intercept = a.get(1) - slope * a.get(0)
//      x_intercept = -y_intercept / slope
//    } else {
//      slope = java.lang.Double.NaN
//      y_intercept = java.lang.Double.POSITIVE_INFINITY
//      x_intercept = a.get(0)
//    }
//  }
//
//
//  fun evaluateY(x: Double): Double {
//    return slope * x + y_intercept
//  }
//
//  override fun integrate(a: Double, b: Double): Double {
//    // integral of y = mx + b is
//    // mx^2/2 + bx + c
//    // at start of integration bound it should be 0
//    val c = -(a * a / 2 + b * a)
//
//    val indefiniteIntegral = { x -> slope * x * x / 2 + y_intercept * x + c }
//
//    return indefiniteIntegral.get(b) - indefiniteIntegral.get(a)
//  }
//
//  fun integrate(): Double {
//    return integrate(x1, x2)
//  }
//
//  fun getPerp(point: ScalarVector): LineR2 {
//    val perpSlope: Double
//    if (java.lang.Double.isNaN(slope)) {
//      perpSlope = 0.0
//    } else {
//      perpSlope = -1 / slope
//    }
//    return LineR2(point, ScalarVector(point.get(0) + 1, (point.get(1) + perpSlope).toFloat()))
//  }
//
//  fun intersection(other: LineR2): ScalarVector? {
//    if (other.slope == slope) {
//      return if (other.x_intercept != other.x_intercept) {
//        null
//      } else {
//        // TODO: is this a good idea to return?
//        ScalarVector(other.x1.toFloat(), other.y2.toFloat())
//      }
//    }
//    if (java.lang.Double.isNaN(slope)) {
//      return ScalarVector(a.get(0), other.evaluateY(a.get(0)).toFloat())
//    }
//
//    if (java.lang.Double.isNaN(other.slope)) {
//      return ScalarVector(other.a.get(0), evaluateY(other.a.get(0)).toFloat())
//    }
//    // mx + b = cx + d
//    // (m-c) x = d - b
//    val x = (other.y_intercept - this.y_intercept) / (this.slope - other.slope)
//    val y = evaluateY(x)
//    return ScalarVector(x.toFloat(), y.toFloat())
//
//
//  }
//}
