package com.team2502.ezauton.utils;

import java.util.Map;

/**
 * An interpolation map that has odd symmetry https://en.wikipedia.org/wiki/Even_and_odd_functions
 */
public class EvenInterpolationMap extends InterpolationMap
{

    public EvenInterpolationMap(Double firstKey, Double firstValue)
    {
        super(firstKey, firstValue);
    }

    public EvenInterpolationMap(Map<Double, Double> initTable)
    {
        super(initTable);
    }

    @Override
    public void putAll(Map<? extends Double, ? extends Double> m)
    {
        if(!MathUtils.Algebra.hasEvenSymmetry(m))
        {
            throw new IllegalArgumentException("Map must be even!");
        }
    }

    @Override
    public Double put(Double key, Double value)
    {
        super.put(-key, value);
        return super.put(key, value);
    }


}