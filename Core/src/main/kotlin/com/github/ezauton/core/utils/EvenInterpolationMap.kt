package com.github.ezauton.core.utils

/**
 * An interpolation map that has even symmetry https://en.wikipedia.org/wiki/Even_and_odd_functions
 */
//class EvenInterpolationMap : LinearInterpolationMap {
//
//  constructor(firstKey: Double?, firstValue: Double?) : super(firstKey, firstValue)
//
//  constructor(initTable: Map<Double, Double>) : super(initTable)
//
//  override fun putAll(m: Map<out Double, Double>) {
//    if (m == null) {
//      return
//    }
//    m.entries.forEach { e -> put(e.key, e.value) }
//  }
//
//  override fun put(key: Double?, value: Double?): Double? {
//    super.put((-key)!!, value)
//    return super.put(key, value)
//  }
//}
