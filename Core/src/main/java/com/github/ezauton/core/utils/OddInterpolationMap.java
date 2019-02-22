package com.github.ezauton.core.utils;

import java.util.Map;

/**
 * An interpolation map that has odd symmetry https://en.wikipedia.org/wiki/Even_and_odd_functions
 */
public class OddInterpolationMap extends InterpolationMap {

    public OddInterpolationMap(Double firstKey, Double firstValue) {
        super(firstKey, firstValue);
    }

    public OddInterpolationMap(Map<Double, Double> initTable) {
        super(initTable);
    }

    @Override
    public void putAll(Map<? extends Double, ? extends Double> m) {
        if (m == null) {
            return;
        }
        m.forEach(this::put);
    }

    @Override
    public Double put(Double key, Double value) {
        super.put(-key, -value);
        return super.put(key, value);
    }


}
