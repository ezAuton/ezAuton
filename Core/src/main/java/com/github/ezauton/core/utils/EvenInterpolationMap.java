package com.github.ezauton.core.utils;

import java.util.Map;
import java.util.Objects;

/**
 * An interpolation map that has odd symmetry https://en.wikipedia.org/wiki/Even_and_odd_functions
 */
public class EvenInterpolationMap extends InterpolationMap {

    public EvenInterpolationMap(Double firstKey, Double firstValue) {
        super(firstKey, firstValue);
    }

    public EvenInterpolationMap(Map<Double, Double> initTable) {
        super(initTable);
    }

    @Override
    public void putAll(Map<? extends Double, ? extends Double> m) {
        if (m == null) {
            return;
        }
        m.entrySet().stream().filter(Objects::nonNull).forEach(e -> put(e.getKey(), e.getValue()));
    }

    @Override
    public Double put(Double key, Double value) {
        super.put(-key, value);
        return super.put(key, value);
    }


}