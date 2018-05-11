package com.team2502.ezauton.trajectory.geometry;

import java.util.Arrays;

public class ImmutableVector {

    private final double[] x;

    public ImmutableVector(double... x)
    {
        this.x = x;
    }

    /**
     *
     * @param size
     * @throws IllegalArgumentException if size does not match
     */
    public void assertSize(int size) throws IllegalArgumentException
    {
        if(getSize() != size)
        {
            throw new IllegalArgumentException("Wrong size vector");
        }
    }

    public static  ImmutableVector of(double element, int size)
    {
        double[] elements = new double[size];
        for (int i = 0; i < size; i++) {
            elements[i] = element;
        }
        return new ImmutableVector(elements);
    }

    public int getSize()
    {
        return x.length;
    }

    public double get(int i)
    {
        return x[i];
    }

    public ImmutableVector add(ImmutableVector other)
    {
        other.assertSize(getSize());
        return applyOperator(other, (first, second) -> first+second);
    }

    public double dot(ImmutableVector other)
    {
        other.assertSize(getSize());
        return mul(other).sum();
    }

    public double dist(ImmutableVector other)
    {
        other.assertSize(getSize());
        ImmutableVector sub = this.sub(other);
        return sub.mag();
    }

    /**
     *
     * @return magnitude squared
     */
    public double mag2()
    {
        return dot(this);
    }

    /**
     *
     * @return magnitude
     */
    public double mag()
    {
        return Math.sqrt(mag2());
    }


    public double sum()
    {
        double val = 0;
        for (double v : x) {
            val+=v;
        }
        return val;
    }

    public ImmutableVector applyOperator(ImmutableVector other, Operator operator)
    {
        double[] temp = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            temp[i] = operator.operate(x[i],other.x[i]);
        }
        return new ImmutableVector(temp);
    }

    public ImmutableVector sub(ImmutableVector other)
    {
        return applyOperator(other, (first, second) -> first-second);
    }

    public ImmutableVector mul(ImmutableVector other)
    {
        return applyOperator(other, (first, second) -> first*second);
    }

    public ImmutableVector mul(double scalar)
    {
        return mul(of(scalar,getSize()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutableVector that = (ImmutableVector) o;
        if(that.getSize() != getSize())
        {
            return false;
        }
        for (int i = 0; i < getSize(); i++) {
            if(Math.abs(that.x[i] - x[i]) > 1E-6) // epsilon eq
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(x);
    }

    @Override
    public String toString() {
        return "ImmutableVector{" +
                "x=" + Arrays.toString(x) +
                '}';
    }

    interface Operator
    {
        double operate(double elementFirst, double elementSecond);
    }
}
