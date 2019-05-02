package com.github.ezauton.core.utils

/**
 * An interpolation map that has odd symmetry https://en.wikipedia.org/wiki/Even_and_odd_functions
 */
class OddInterpolationMap : LinearInterpolationMap {

    constructor(firstKey: Double?, firstValue: Double?) : super(firstKey, firstValue)

    constructor(initTable: Map<Double, Double>) : super(initTable)

    override fun putAll(m: Map<out Double, Double>) {
        if (m == null) {
            return
        }
        m.forEach(BiConsumer<out Double, out Double> { key, value -> this.put(key, value) })
    }

    override fun put(key: Double?, value: Double?): Double? {
        super.put((-key)!!, (-value)!!)
        return super.put(key, value)
    }
}
