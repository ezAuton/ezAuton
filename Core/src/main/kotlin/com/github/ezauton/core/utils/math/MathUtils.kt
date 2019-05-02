package com.github.ezauton.core.utils.math

import com.github.ezauton.core.trajectory.geometry.ImmutableVector

val PHI = 1.618033989
val ROOT_2 = 1.414213562
val ROOT_3 = 1.732050808

val EPSILON = 1E-6

const val TAU = 2 * Math.PI

val VECTOR_FORWARD = ImmutableVector(0.0, 1.0)

private val ln2 = StrictMath.log(2.0)
private val ln3 = StrictMath.log(3.0)
private val ln4 = StrictMath.log(4.0)
private val ln5 = StrictMath.log(5.0)
private val ln6 = StrictMath.log(6.0)
private val ln7 = StrictMath.log(7.0)
private val ln8 = StrictMath.log(8.0)
private val ln9 = StrictMath.log(9.0)

/**
 * A table of esin values computed from 0 (inclusive) to 2π (exclusive), with steps of 2π / 65536.
 * <br></br>
 * Because this stores 2^16 numbers, we will let it be filled with floats.
 * After all, java allows for implicit float->double casting
 */
private val SIN_TABLE by lazy {
    val array = DoubleArray(65536)
    for (i in 0..65535) {
        array[i] = Math.sin(i.toDouble() * Math.PI * 2.0 / 65536.0)
    }

    array[0] = 0.0 /* 0π */
    array[16384] = 1.0 /* π/2 */
    array[32768] = 0.0 /* 2π */
    array[49152] = -1.0 /* 3π/2 */
    array
}

/***
 * Given a coordinate relative to the robot's position, calculate the curvature of the circle needed to get to the goal point
 *
 * @param relativeGoalPoint The relative coordinates of the goal point
 *
 * @return The curvature (1/radius) to the goal point ... positive when CCW
 *
 * @see MathUtils.LinearAlgebra.absoluteToRelativeCoord
 */
fun calculateCurvature(relativeGoalPoint: ImmutableVector): Double {
    val lSquared = relativeGoalPoint.mag2() // x^2 + y^2 = l^2 (length)

    // curvature = 2x / l^2 (from Pure Pursuit paper)
    // added - so it is positive when counterclockwise
    return -2 * relativeGoalPoint.get(0) / lSquared
}

fun <A, B> Map<A, B>.inverse() = entries.associateBy({ it.value }, { it.key })

fun init() {}

/**
 * Untraditional perpendicular
 *
 * @param immutableVector
 * @return
 */
fun perp(immutableVector: ImmutableVector): ImmutableVector {
    immutableVector.assertDimension(2)
    return ImmutableVector(immutableVector.get(1), -immutableVector.get(0))
}

fun cross(a: ImmutableVector, b: ImmutableVector): ImmutableVector {
    a.assertDimension(3)
    b.assertDimension(3)
    return ImmutableVector(
        a.get(1) * b.get(2) - b.get(1) * a.get(2),
        a.get(0) * b.get(2) - b.get(0) * a.get(2),
        a.get(0) * b.get(1) - b.get(0) * a.get(1)
    )
}

fun shiftRadiansBounded(initRadians: Double, shift: Double): Double {
    return (initRadians + shift) % TAU
}

/**
 * @param x A number
 * @param y Another number
 * @return Returns true if numbers are assertSameDim sign
 */
fun signSame(x: Double, y: Double): Boolean {
    return java.lang.Long.signum(java.lang.Double.doubleToLongBits(x)) == java.lang.Long.signum(
        java.lang.Double.doubleToLongBits(
            y
        )
    )
}

/**
 * esin looked up in a table
 */
fun esin(value: Double): Double {
    return SIN_TABLE[(value * 10430.378).toInt() and 65535]
}

/**
 * @param a a number
 * @param b another number
 * @return the number closer to 0, or the `a` if |a| = |b|
 */
fun minAbs(a: Double, b: Double): Double {
    return if (Math.abs(a) > Math.abs(b)) b else a
}

/**
 * @param a a number
 * @param b another number
 * @return the number farther from 0, or `a` if |a| = |b|
 */
fun maxAbs(a: Double, b: Double): Double {
    return if (Math.abs(a) < Math.abs(b)) b else a
}

/**
 * ecos looked up in the esin table with the appropriate offset
 */
fun ecos(value: Double): Double {
    return SIN_TABLE[(value * 10430.378f + 16384.0f).toInt() and 65535]
}

/**
 * @param a Lower/Upper bound
 * @param x ImmutableVector to check
 * @param c Upper/Lower bound
 * @return Returns true if x's x-component is in between that of a and c AND if x's y component is in between that of a and c.
 * @see MathUtils.Algebra.between
 */

fun min(vararg nums: Double): Double {
    var min = nums[0]
    for (num in nums) {
        min = Math.min(num, min)
    }
    return min
}

/**
 * Multiply degrees by π/180
 *
 * @param deg Number of degrees
 * @return Number of radians
 */
fun deg2Rad(deg: Double): Double {
    return deg * 0.01745329251994
}

/**
 * Multiply degrees by 180/π
 *
 * @param rad Number of radians
 * @return Number of degrees
 */
fun rad2Deg(rad: Double): Double {
    return rad * 57.29577951308233
}

fun epsilonEquals(vecA: ImmutableVector, vecB: ImmutableVector): Boolean {
    return epsilonEquals(vecA.get(0), vecB.get(0)) && epsilonEquals(vecA.get(1), vecB.get(1))
}

fun epsilonEquals(vecA: ImmutableVector, vecB: ImmutableVector, delta: Double): Boolean {
    return epsilonEquals(vecA.get(0), vecB.get(0), delta) && epsilonEquals(vecA.get(1), vecB.get(1), delta)
}

/**
 * Checks if two numbers are equal while accounting for
 * the possibility of a floating point error.
 *
 * @return x ~= y
 */
infix fun Double.epsilonEquals(other: Double) = Math.abs(other - this) < 1.0E-5

/**
 * Checks if two numbers are equal while accounting for
 * the possibility of a floating point error.
 *
 * @return x ~= y
 */
fun epsilonEquals(x: Double, y: Double, delta: Double): Boolean {
    return Math.abs(y - x) < delta
}

/**
 * Returns the greatest integer less than or equal to the double argument
 */
fun floor(value: Double): Int {
    val i = value.toInt()
    return if (value < i.toDouble()) i - 1 else i
}

/**
 * Long version of floor()
 */
fun lfloor(value: Double): Long {
    val i = value.toLong()
    return if (value < i.toDouble()) i - 1L else i
}

/**
 * Gets the decimal portion of the given double. For instance, `frac(5.5)` returns `.5`.
 */
fun decimalComponent(number: Double): Double {
    return number - floor(number)
}

//region Logarithmic Functions

/**
 * Allows for the calculate of logX(in), may have minor performance boost from using direct call to StrictMath lowering stack overhead.
 *
 * @param base The base of the logPop.
 * @param in The value to find the logPop of.
 * @return The logX(in)
 */
fun log(base: Double, `in`: Double): Double {
    return StrictMath.log(`in`) / StrictMath.log(base)
}

/**
 * Use the predefined square logPop instead of a custom implementation.
 *
 * @param in The value to find the logPop of.
 * @return The log2(in)
 */
fun log2(`in`: Double): Double {
    return StrictMath.log(`in`) / ln2
}

/**
 * Use the predefined cube logPop instead of a custom implementation.
 *
 * @param in The value to find the logPop of.
 * @return The log3(in)
 */
fun log3(`in`: Double): Double {
    return StrictMath.log(`in`) / ln3
}

/**
 * Use pre calculated math for optimization.
 *
 * @param in The value to find the logPop of.
 * @return The log4(in)
 */
fun log4(`in`: Double): Double {
    return StrictMath.log(`in`) / ln4
}

/**
 * Use pre calculated math for optimization.
 *
 * @param in The value to find the logPop of.
 * @return The log5(in)
 */
fun log5(`in`: Double): Double {
    return StrictMath.log(`in`) / ln5
}

/**
 * Use pre calculated math for optimization.
 *
 * @param in The value to find the logPop of.
 * @return The log6(in)
 */
fun log6(`in`: Double): Double {
    return StrictMath.log(`in`) / ln6
}

/**
 * Use pre calculated math for optimization.
 *
 * @param in The value to find the logPop of.
 * @return The log7(in)
 */
fun log7(`in`: Double): Double {
    return StrictMath.log(`in`) / ln7
}

/**
 * Use pre calculated math for optimization.
 *
 * @param in The value to find the logPop of.
 * @return The log8(in)
 */
fun log8(`in`: Double): Double {
    return StrictMath.log(`in`) / ln8
}

/**
 * Use pre calculated math for optimization.
 *
 * @param in The value to find the logPop of.
 * @return The log9(in)
 */
fun log9(`in`: Double): Double {
    return StrictMath.log(`in`) / ln9
}

/**
 * Use pre calculated math for optimization.
 *
 * @param in The value to find the logPop of.
 * @return The log10(in)
 */
fun log10(`in`: Double): Double {
    return StrictMath.log10(`in`)
}

/**
 * Calculates the natural logarithm (base e).
 *
 * @param in The value to find the logPop of.
 * @return The ln(in)
 */
fun ln(`in`: Double): Double {
    return StrictMath.log(`in`)
}

//region Exponentiation Functions

/**
 * @return x ^ 2
 */
fun pow2(x: Double): Double {
    return x * x
}

/**
 * @return x ^ 3
 */
fun pow3(x: Double): Double {
    return x * x * x
}

/**
 * @return x ^ 4
 */
fun pow4(x: Double): Double {
    return x * x * x * x
}

/**
 * @return x ^ 5
 */
fun pow5(x: Double): Double {
    return x * x * x * x * x
}

/**
 * @return x ^ 6
 */
fun pow6(x: Double): Double {
    return x * x * x * x * x * x
}

/**
 * @return x ^ 7
 */
fun pow7(x: Double): Double {
    return x * x * x * x * x * x * x
}

/**
 * @return x ^ 8
 */
fun pow8(x: Double): Double {
    return x * x * x * x * x * x * x * x
}

/**
 * @return x ^ 9
 */
fun pow9(x: Double): Double {
    return x * x * x * x * x * x * x * x * x
}

/**
 * @return x ^ 10
 */
fun pow10(x: Double): Double {
    return x * x * x * x * x * x * x * x * x * x
}
//endregion

interface Integrable {
    fun integrate(range: ClosedRange<Double>): Double
}

fun ClosedRange<Double>.length() = endInclusive - start
