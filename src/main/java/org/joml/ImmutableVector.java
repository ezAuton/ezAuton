/*
 * (C) Copyright 2015-2018 Richard Greenlees

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

 */
package org.joml;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

/**
 * Represents a 2D vector with single-precision.
 *
 * @author RGreenlees
 * @author Kai Burjack
 */
public class ImmutableVector
{
    public static final int numberFormatDecimals = Integer.parseInt(System.getProperty("joml.format.decimals", "3"));
    public static final boolean useNumberFormat = hasOption(System.getProperty("joml.format", "true"));
    public static final NumberFormat NUMBER_FORMAT = decimalFormat();
    private static final long serialVersionUID = 1L;
    /**
     * The x component of the vector.
     */
    public final double x;
    /**
     * The y component of the vector.
     */
    public final double y;
    public ImmutableVector(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Create a new {@link ImmutableVector} and initialize both of its components with the given value.
     *
     * @param d the value of both components
     */
    public ImmutableVector(double d)
    { this(d, d); }

    /* (non-Javadoc)
     * @see org.joml.api.ImmutableVector#y()
     */

    /**
     * Create a new {@link ImmutableVector} and initialize its components to zero.
     */
    public ImmutableVector()
    { this(0.0F); }

    /* (non-Javadoc)
     * @see org.joml.api.ImmutableVector#getF(int)
     */

    /**
     * Create a new {@link ImmutableVector} and initialize its components to the one of the given vector.
     *
     * @param v the {@link ImmutableVector} to copy the values from
     */
    public ImmutableVector(ImmutableVector v)
    {
        x = v.x();
        y = v.y();
    }

    public static boolean epsilonEquals(final double a, final double b)
    { return Math.abs(b - a) < 1.0E-5F; }

    private static boolean hasOption(String v)
    {
        if(v == null) { return false; }
        if(v.trim().isEmpty()) { return true; }
        return Boolean.valueOf(v);
    }

    private static NumberFormat decimalFormat()
    {
        NumberFormat df;
        if(useNumberFormat)
        {
            char[] prec = new char[numberFormatDecimals];
            Arrays.fill(prec, '0');
            df = new DecimalFormat(" 0." + new String(prec) + "E0;-");
        }
        else
        {
            df = NumberFormat.getNumberInstance(Locale.ENGLISH);
            df.setGroupingUsed(false);
        }
        return df;
    }

    public static String formatNumbers(String str)
    {
        StringBuffer res = new StringBuffer();
        int eIndex = Integer.MIN_VALUE;
        for(int i = 0; i < str.length(); i++)
        {
            char c = str.charAt(i);
            if(c == 'E') { eIndex = i; }
            else if(c == ' ' && eIndex == i - 1)
            {
                // workaround Java 1.4 DecimalFormat bug
                res.append('+');
                continue;
            }
            else if(Character.isDigit(c) && eIndex == i - 1) { res.append('+'); }
            res.append(c);
        }
        return res.toString();
    }

    /* (non-Javadoc)
     * @see org.joml.api.ImmutableVector#dot(org.joml.api.ImmutableVector)
     */

    /* (non-Javadoc)
     * @see org.joml.api.ImmutableVector#x()
     */
    public double x()
    { return this.x; }

    /* (non-Javadoc)
     * @see org.joml.api.ImmutableVector#angle(org.joml.api.ImmutableVector)
     */

    public double y()
    { return this.y; }

    /* (non-Javadoc)
     * @see org.joml.api.ImmutableVector#length()
     */

    public double get(int index) throws IndexOutOfBoundsException
    {
        switch(index)
        {
            case 0:
                return x;
            case 1:
                return y;
            default:
                throw new IndexOutOfBoundsException("Index must be in the range of [0,1].");
        }
    }

    /**
     * Return a new vector that is perpendicular to this one
     *
     * @return A perpendicular vector
     */
    public ImmutableVector perpendicular()
    { return new ImmutableVector(y, x * -1); }

    /**
     * Subtract <code>v</code> from this vector.
     *
     * @param v the vector to subtract
     * @return the difference
     */
    public ImmutableVector sub(ImmutableVector v)
    { return new ImmutableVector(x - v.x(), y - v.y()); }

    /**
     * Subtract a scalar amount from the x and y components
     * @param scalar The scalar amount
     * @return the difference
     */
    public ImmutableVector sub(double scalar)
    { return new ImmutableVector(x - scalar, y - scalar); }

    /**
     * Subtract <tt>(x, y)</tt> from this vector.
     *
     * @param x the x component to subtract
     * @param y the y component to subtract
     * @return the difference
     */
    public ImmutableVector sub(double x, double y)
    { return new ImmutableVector(this.x - x, this.y - y); }

    public double dot(ImmutableVector v)
    { return x * v.x() + y * v.y(); }

    public double angle(ImmutableVector v)
    {
        double dot = x * v.x() + y * v.y();
        double det = x * v.y() - y * v.x();
        return (double) Math.atan2(det, dot);
    }

    public double length()
    { return (double) Math.sqrt((x * x) + (y * y)); }

    /* (non-Javadoc)
     * @see org.joml.api.ImmutableVector#lengthSquared()
     */
    public double lengthSquared()
    { return x * x + y * y; }

    /* (non-Javadoc)
     * @see org.joml.api.ImmutableVector#distance(double, double)
     */
    public double distance(double x, double y)
    {
        double dx = this.x - x;
        double dy = this.y - y;
        return (double) Math.sqrt(dx * dx + dy * dy);
    }

    /* (non-Javadoc)
     * @see org.joml.api.ImmutableVector#distanceSquared(double, double)
     */
    public double distanceSquared(double x, double y)
    {
        double dx = this.x - x;
        double dy = this.y - y;
        return dx * dx + dy * dy;
    }

    /* (non-Javadoc)
     * @see org.joml.api.ImmutableVector#distance(org.joml.api.ImmutableVector)
     */
    public double distance(ImmutableVector v)
    { return distance(v.x(), v.y()); }

    /* (non-Javadoc)
     * @see org.joml.api.ImmutableVector#distanceSquared(org.joml.api.ImmutableVector)
     */
    public double distanceSquared(ImmutableVector v)
    { return distanceSquared(v.x(), v.y()); }

    /**
     * Normalize this vector.
     *
     * @return this
     */
    public ImmutableVector normalize()
    {
        double invLength = (double) (1.0 / Math.sqrt(x * x + y * y));
        return new ImmutableVector(x * invLength, y * invLength);
    }

    /**
     * Scale this vector to have the given length.
     *
     * @param length the desired length
     * @return this
     */
    public ImmutableVector normalize(double length)
    {
        double invLength = (double) (1.0 / Math.sqrt(x * x + y * y)) * length;
        return new ImmutableVector(x * invLength, y * invLength);
    }

    /**
     * Add <code>v</code> to this vector.
     *
     * @param v the vector to add
     * @return this
     */
    public ImmutableVector add(ImmutableVector v)
    { return new ImmutableVector(x + v.x(), y + v.y()); }

    public ImmutableVector add(double scalar)
    {
        return new ImmutableVector(x + scalar, y + scalar);
    }

    /**
     * Increment the components of this vector by the given values.
     *
     * @param x the x component to add
     * @param y the y component to add
     * @return this
     */
    public ImmutableVector add(double x, double y)
    { return new ImmutableVector(x + x, y + y); }

    /**
     * Negate this vector.
     *
     * @return this
     */
    public ImmutableVector negate()
    { return new ImmutableVector(-x, -y); }

    /**
     * Multiply the components of this vector by the given scalar.
     *
     * @param scalar the value to multiply this vector's components by
     * @return this
     */
    public ImmutableVector mul(double scalar)
    { return new ImmutableVector(x * scalar, y * scalar); }

    /**
     * Multiply the components of this Vector2f by the given scalar values and store the result in <code>this</code>.
     *
     * @param x the x component to multiply this vector by
     * @param y the y component to multiply this vector by
     * @return this
     */
    public ImmutableVector mul(double x, double y)
    { return new ImmutableVector(this.x * x, this.y * y); }

    /**
     * Multiply this Vector2f component-wise by another Vector2f.
     *
     * @param v the vector to multiply by
     * @return this
     */
    public ImmutableVector mul(ImmutableVector v)
    { return new ImmutableVector(x * v.x(), y * v.y()); }

    public ImmutableVector div(ImmutableVector v)
    { return new ImmutableVector(x / v.x(), y / v.y()); }

    public ImmutableVector div(double scalar)
    { return new ImmutableVector(x / scalar, y / scalar); }

    /**
     * Linearly interpolate <code>this</code> and <code>other</code> using the given interpolation factor <code>t</code>
     * and store the result in <code>this</code>.
     * <p>
     * If <code>t</code> is <tt>0.0</tt> then the result is <code>this</code>. If the interpolation factor is <code>1.0</code>
     * then the result is <code>other</code>.
     *
     * @param other the other vector
     * @param t     the interpolation factor between 0.0 and 1.0
     * @return this
     */
    public ImmutableVector lerp(ImmutableVector other, double t)
    { return new ImmutableVector(x + (other.x() - x) * t, y + (other.y() - y) * t); }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj) { return true; }
        if(obj == null) { return false; }
        if(getClass() != obj.getClass()) { return false; }
        ImmutableVector other = (ImmutableVector) obj;
        if(!epsilonEquals(x, other.x)) { return false; }
        if(!epsilonEquals(y, other.y)) { return false; }
        return true;
    }

    /**
     */
    @Override
    public String toString()
    {
        return "ImmutableVector{" +
               "x=" + x +
               ", y=" + y +
               '}';
    }

    /**
     * Return a string representation of this vector by formatting the vector components with the given {@link NumberFormat}.
     *
     * @param formatter the {@link NumberFormat} used to format the vector components with
     * @return the string representation
     */
    public String toString(NumberFormat formatter)
    { return "(" + formatter.format(x) + " " + formatter.format(y) + ")"; }

    /**
     * Add the component-wise multiplication of <code>a * b</code> to this vector.
     *
     * @param a the first multiplicand
     * @param b the second multiplicand
     * @return this
     */
    public ImmutableVector fma(ImmutableVector a, ImmutableVector b)
    { return new ImmutableVector(x + a.x() * b.x(), y + a.y() * b.y()); }

    /**
     * Add the component-wise multiplication of <code>a * b</code> to this vector.
     *
     * @param a the first multiplicand
     * @param b the second multiplicand
     * @return this
     */
    public ImmutableVector fma(double a, ImmutableVector b)
    { return new ImmutableVector(x + a * b.x(), y + a * b.y()); }
}
