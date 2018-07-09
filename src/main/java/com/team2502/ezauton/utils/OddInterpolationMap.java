package com.team2502.ezauton.utils;

import java.util.Map;

/**
 * An interpolation map that has odd symmetry https://en.wikipedia.org/wiki/Even_and_odd_functions
 */
public class OddInterpolationMap extends InterpolationMap
{

    public OddInterpolationMap(Double firstKey, Double firstValue)
    {
        super(firstKey, firstValue);
    }

    public OddInterpolationMap(Map<Double, Double> initTable)
    {
        super(initTable);
    }

    @Override
    public void putAll(Map<? extends Double, ? extends Double> m)
    {
        if(!MathUtils.Algebra.hasOddSymmetry(m))
        {
            throw new IllegalArgumentException("Map must be odd!");
        }
    }

    @Override
    public Double put(Double key, Double value)
    {
        super.put(-key, -value);
        return super.put(key, value);
    }


}
