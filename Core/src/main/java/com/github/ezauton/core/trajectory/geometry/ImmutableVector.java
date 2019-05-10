package com.github.ezauton.core.trajectory.geometry;

import com.github.ezauton.core.utils.MathUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * An n-dimensional, immutable vector.
 */
public class ImmutableVector implements Serializable, Iterable<Double> {
    private double[] elements;

    public ImmutableVector(double... x) {
        this.elements = x;
    }

    public ImmutableMatrix T() {
        List<ImmutableVector> t = Arrays.stream(elements).mapToObj(ImmutableVector::new).collect(Collectors.toList());
        return new ImmutableMatrix(t);
    }

    /**
     * Convert a list into an {@link ImmutableVector}
     *
     * @param list
     */
    public ImmutableVector(List<Double> list) {
        elements = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            elements[i] = list.get(i);
        }
    }

    /**
     * A 0-dimensional ImmutableVector... how sad 😭
     */
    public ImmutableVector() {
    }

    public static ImmutableVector of(double element, int size) {
        double[] elements = new double[size];
        for (int i = 0; i < size; i++) {
            elements[i] = element;
        }
        return new ImmutableVector(elements);
    }

    /**
     * throws error if not same dimension
     *
     * @param vectors
     */
    public static void assertSameDim(Collection<ImmutableVector> vectors) {
        int initSize = -1;
        for (ImmutableVector vector : vectors) {
            if (initSize == -1) {
                initSize = vector.getDimension();
            } else {
                vector.assertSize(initSize);
            }
        }
    }

    /**
     * @param size The dimension of the vector.
     * @return
     */
    public static ImmutableVector origin(int size) {
        return of(0, size);
    }

    public ImmutableVector norm() {
        return div(mag());
    }

    public double[] getElements() {
        return elements;
    }

//    public Pair<ImmutableVector,ImmutableVector> split(int min, int max)
//    {
//        // TODO
//    }

    /**
     * @param size
     * @throws IllegalArgumentException if size does not match
     */
    public void assertSize(int size) throws IllegalArgumentException {
        if (getDimension() != size) {
            throw new IllegalArgumentException("Wrong size vector");
        }
    }

    public int getDimension() {
        return elements.length;
    }

    public double get(int i) {
        return elements[i];
    }

    public ImmutableVector add(ImmutableVector other) {
        other.assertSize(getDimension());
        return applyOperator(other, (first, second) -> first + second);
    }

    public double dot(ImmutableVector other) {
        other.assertSize(getDimension());
        return mul(other).sum();
    }

    public double dist(ImmutableVector other) {
        other.assertSize(getDimension());
        ImmutableVector sub = this.sub(other);
        return sub.mag();
    }

    public double dist2(ImmutableVector other) {
        other.assertSize(getDimension());
        ImmutableVector sub = this.sub(other);
        return sub.mag2();
    }

    /**
     * @return magnitude squared
     */
    public double mag2() {
        return dot(this);
    }

    /**
     * @return magnitude
     */
    public double mag() {
        return Math.sqrt(mag2());
    }


    public double sum() {
        double val = 0;
        for (double v : elements) {
            val += v;
        }
        return val;
    }

    public ImmutableVector applyOperator(ImmutableVector other, Operator operator) {
        double[] temp = new double[elements.length];
        for (int i = 0; i < elements.length; i++) {
            temp[i] = operator.operate(elements[i], other.elements[i]);
        }
        return new ImmutableVector(temp);
    }

    public ImmutableVector sub(ImmutableVector other) {
        return applyOperator(other, (first, second) -> first - second);
    }

    public ImmutableVector mul(ImmutableVector other) {
        return applyOperator(other, (first, second) -> first * second);
    }

    public ImmutableVector div(ImmutableVector other) {
        return applyOperator(other, (first, second) -> first / second);
    }

    public DoubleStream stream() {
        return Arrays.stream(elements);
    }

    public boolean isFinite() {
        return stream().allMatch(Double::isFinite);
    }

    /**
     * Remove instances of a number from a vector
     *
     * @param toTruncate The number to remove
     * @return A new vector that does not have instances of that number
     */
    public ImmutableVector truncateElement(double toTruncate) {
        List<Double> toReturn = new ArrayList<>(getDimension());
        for (double element : elements) {
            if (toTruncate != element) {
                toReturn.add(element);
            }
        }
        return new ImmutableVector(toReturn);
    }

    public ImmutableVector rotate2DCCW(double theta) {
        return MathUtils.LinearAlgebra.rotate2D(this, theta);
        // TODO: test
    }

    public ImmutableVector rotate2DCW(double theta) {
        return MathUtils.LinearAlgebra.rotate2D(this, -theta);
        // TODO: test
    }

    public ImmutableVector mul(double scalar) {
        return mul(of(scalar, getDimension()));
    }

    public ImmutableVector div(double scalar) {
        return mul(1D / scalar);
    }

    /**
     * @param o
     * @return If epsilon equals
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImmutableVector that = (ImmutableVector) o;
        if (that.getDimension() != getDimension()) {
            return false;
        }
        for (int i = 0; i < getDimension(); i++) {
            if (Math.abs(that.elements[i] - elements[i]) > 1E-4) // epsilon eq // TODO: make this better documented
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(elements);
    }

    @Override
    public String toString() {
        return "ImmutableVector{" +
                "elements=" + Arrays.toString(elements) +
                '}';
    }

    @NotNull
    @Override
    public Iterator<Double> iterator() {
        return new Iterator<Double>() {
            int elementOn = 0;

            @Override
            public boolean hasNext() {
                return elementOn < getDimension();
            }

            @Override
            public Double next() {
                double result = elements[elementOn];
                elementOn++;
                return result;
            }
        };
    }

    interface Operator {
        double operate(double elementFirst, double elementSecond);
    }
}
