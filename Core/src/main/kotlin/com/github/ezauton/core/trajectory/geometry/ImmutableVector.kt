package com.github.ezauton.core.trajectory.geometry

import java.io.Serializable
import java.util.*
import java.util.stream.DoubleStream


typealias Operator = (Double, Double) -> Double

/**
 * An n-dimensional, immutable vector.
 */
class ImmutableVector : Serializable, Comparable<ImmutableVector> {
    override fun compareTo(other: ImmutableVector): Int {
        other.assertDimension(dimension)
        for (i in 0 until dimension) {
            val a = get(i)
            val b = other[i]
            if (a != b) return a.compareTo(b)
        }
        return 0
    }

    val elements: DoubleArray

    val dimension: Int get() = elements.size

    val isFinite: Boolean get() = elements.all { it.isFinite() }

    constructor(vararg x: Double) {
        this.elements = x
    }

    /**
     * Convert a list into an [ImmutableVector]
     *
     * @param list
     */
    constructor(list: List<Double>) {
        elements = DoubleArray(list.size)
        for (i in list.indices) {
            elements[i] = list[i]
        }
    }

    /**
     * A 0-dimensional ImmutableVector... how sad 😭
     */
    constructor() {
        elements = DoubleArray(0)
    }

    fun norm(): ImmutableVector {
        return div(mag())
    }

    /**
     * @param size
     * @throws IllegalArgumentException if size does not match
     */
    @Throws(IllegalArgumentException::class)
    fun assertDimension(size: Int) {
        if (dimension != size) {
            throw IllegalArgumentException("Wrong size vector")
        }
    }

    operator fun get(i: Int): Double {
        return elements[i]
    }

    operator fun plus(other: ImmutableVector): ImmutableVector {
        other.assertDimension(dimension)
        return applyOperator(other) { first, second -> first + second }
    }

    fun dot(other: ImmutableVector): Double {
        other.assertDimension(dimension)
        return mul(other).sum()
    }

    fun dist(other: ImmutableVector): Double {
        other.assertDimension(dimension)
        val sub = this.sub(other)
        return sub.mag()
    }

    fun dist2(other: ImmutableVector): Double {
        other.assertDimension(dimension)
        val sub = this.sub(other)
        return sub.mag2()
    }

    /**
     * @return magnitude squared
     */
    fun mag2(): Double {
        return dot(this)
    }

    /**
     * @return magnitude
     */
    fun mag(): Double {
        return Math.sqrt(mag2())
    }


    fun sum(): Double {
        return elements.sum()
    }

    fun applyOperator(other: ImmutableVector, operator: Operator): ImmutableVector {
        val temp = DoubleArray(elements.size)
        for (i in elements.indices) {
            temp[i] = operator(elements[i], other.elements[i])
        }
        return ImmutableVector(*temp)
    }

    fun sub(other: ImmutableVector): ImmutableVector {
        return applyOperator(other) { first, second -> first - second }
    }

    fun mul(other: ImmutableVector): ImmutableVector {
        return applyOperator(other) { first, second -> first * second }
    }

    operator fun div(other: ImmutableVector): ImmutableVector {
        return applyOperator(other) { first, second -> first / second }
    }

    fun stream(): DoubleStream {
        return Arrays.stream(elements)
    }

    fun iterator(): DoubleIterator {
        return elements.iterator()
    }

    /**
     * Remove instances of a number from a vector
     *
     * @param toTruncate The number to remove
     * @return A new vector that does not have instances of that number
     */
    fun truncateElement(toTruncate: Double): ImmutableVector {
        val toReturn = ArrayList<Double>(dimension)
        for (element in elements) {
            if (toTruncate != element) {
                toReturn.add(element)
            }
        }
        return ImmutableVector(toReturn)
    }

    fun mul(scalar: Double): ImmutableVector {
        return mul(of(scalar, dimension))
    }

    operator fun div(scalar: Double): ImmutableVector {
        return mul(1.0 / scalar)
    }

    /**
     * @param other
     * @return If epsilon equals
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as ImmutableVector?
        if (that!!.dimension != dimension) {
            return false
        }
        for (i in 0 until dimension) {
            if (Math.abs(that.elements[i] - elements[i]) > 1E-6)
            // epsilon eq
            {
                return false
            }
        }
        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(elements)
    }

    override fun toString(): String {
        return "ImmutableVector{" +
                "elements=" + Arrays.toString(elements) +
                '}'.toString()
    }

    companion object {

        fun of(element: Double, size: Int): ImmutableVector {
            val elements = DoubleArray(size)
            for (i in 0 until size) {
                elements[i] = element
            }
            return ImmutableVector(*elements)
        }

        /**
         * throws error if not same dimension
         *
         * @param vectors
         */
        fun assertSameDim(vectors: Collection<ImmutableVector>) {
            var initSize = -1
            for (vector in vectors) {
                if (initSize == -1) {
                    initSize = vector.dimension
                } else {
                    vector.assertDimension(initSize)
                }
            }
        }

        /**
         * @param size The dimension of the vector.
         * @return
         */
        fun origin(size: Int): ImmutableVector {
            return of(0.0, size)
        }
    }

    operator fun rangeTo(other: ImmutableVector): ClosedRange<ImmutableVector> = object : ClosedRange<ImmutableVector> {
        override val endInclusive: ImmutableVector
            get() = this@ImmutableVector
        override val start: ImmutableVector
            get() = other

    }
}

fun vec(vararg x: Double) = ImmutableVector(*x)
