package com.github.ezauton.core.utils.math

import java.util.*

/**
 * @param map
 * @return If is odd function
 */
fun hasOddSymmetry(map: Map<out Double, Double>): Boolean {
    return map.entries.stream().allMatch { entry ->
        val key = entry.key
        val value = entry.value
        val symmetricEntry = map[-key].toDouble()
        symmetricEntry != null && symmetricEntry == -value
    }
}

/**
 * @param map
 * @return If is even function
 */
fun hasEvenSymmetry(map: Map<out Double, Double>): Boolean {
    return map.entries.stream().allMatch { entry ->
        val key = entry.key
        val value = entry.value
        val symmetricValue = map[-key].toDouble()
        symmetricValue != null && symmetricValue == value
    }
}

/**
 * Solve for the roots of a quadratic of the form ax^2 + bx + c
 *
 * @param a x^2 coefficient
 * @param b x coefficient
 * @param c added constant
 * @return roots of the quadratic
 */
fun quadratic(a: Double, b: Double, c: Double): Set<Double> {
    val toReturn = HashSet<Double>()
    val discriminate = discriminate(a, b, c)
    if (discriminate < 0) {
        return toReturn
    } else if (discriminate == 0.0) {
        toReturn.add(-b / (2 * a))
    } else {
        val LHS = -b / (2 * a)
        val RHS = Math.sqrt(discriminate) / (2 * a)
        toReturn.add(LHS + RHS)
        toReturn.add(LHS - RHS)
    }
    return toReturn
}

/**
 * Solve for the discriminant of a quadratic of the form ax^2 + bx + c
 *
 * @param a x^2 coefficient
 * @param b x coefficient
 * @param c added thing
 * @return roots of the quadratic
 * @see MathUtils.Algebra.quadratic
 */
fun discriminate(a: Double, b: Double, c: Double): Double {
    return b * b - 4.0 * a * c
}

/**
 * @return if a <= x <= b or b<= x <= a
 */
fun between(a: Double, x: Double, b: Double): Boolean {
    return bounded(a, x, b) || bounded(b, x, a)
}


/**
 * @param a lower bound
 * @param x some number
 * @param b upper bound
 * @return if x is between lower and upper bound
 */
fun bounded(a: Double, x: Double, b: Double): Boolean {
    return x in a..b
}
