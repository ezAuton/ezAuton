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
public class ImmutableVector2f
{
    public static final int numberFormatDecimals = Integer.parseInt(System.getProperty("joml.format.decimals", "3"));
    public static final boolean useNumberFormat = hasOption(System.getProperty("joml.format", "true"));
    public static final NumberFormat NUMBER_FORMAT = decimalFormat();
    private static final long serialVersionUID = 1L;
    /**
     * The x component of the vector.
     */
    public final float x;
    /**
     * The y component of the vector.
     */
    public final float y;

    /**
     * Create a new {@link ImmutableVector2f} and initialize its components to the given values.
     *
     * @param x the x component
     * @param y the y component
     */
    public ImmutableVector2f(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Create a new {@link ImmutableVector2f} and initialize both of its components with the given value.
     *
     * @param d the value of both components
     */
    public ImmutableVector2f(float d)
    { this(d, d); }

    /* (non-Javadoc)
     * @see org.joml.api.ImmutableVector2f#y()
     */

    /**
     * Create a new {@link ImmutableVector2f} and initialize its components to zero.
     */
    public ImmutableVector2f()
    { this(0.0F); }

    /* (non-Javadoc)
     * @see org.joml.api.ImmutableVector2f#getF(int)
     */

    /**
     * Create a new {@link ImmutableVector2f} and initialize its components to the one of the given vector.
     *
     * @param v the {@link ImmutableVector2f} to copy the values from
     */
    public ImmutableVector2f(ImmutableVector2f v)
    {
        x = v.x();
        y = v.y();
    }

    public static boolean epsilonEquals(final float a, final float b)
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
     * @see org.joml.api.ImmutableVector2f#dot(org.joml.api.ImmutableVector2f)
     */

    /* (non-Javadoc)
     * @see org.joml.api.ImmutableVector2f#x()
     */
    public float x()
    { return this.x; }

    /* (non-Javadoc)
     * @see org.joml.api.ImmutableVector2f#angle(org.joml.api.ImmutableVector2f)
     */

    public float y()
    { return this.y; }

    /* (non-Javadoc)
     * @see org.joml.api.ImmutableVector2f#length()
     */

    public float get(int index) throws IndexOutOfBoundsException
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
     * Set this vector to be one of its perpendicular vectors.
     *
     * @return this
     */
    public ImmutableVector2f perpendicular()
    { return new ImmutableVector2f(y, x * -1); }

    /**
     * Subtract <code>v</code> from this vector.
     *
     * @param v the vector to subtract
     * @return this
     */
    public ImmutableVector2f sub(ImmutableVector2f v)
    { return new ImmutableVector2f(x - v.x(), y - v.y()); }

    public ImmutableVector2f sub(float scalar)
    { return new ImmutableVector2f(x - scalar, y - scalar); }

    /**
     * Subtract <tt>(x, y)</tt> from this vector.
     *
     * @param x the x component to subtract
     * @param y the y component to subtract
     * @return this
     */
    public ImmutableVector2f sub(float x, float y)
    { return new ImmutableVector2f(this.x - x, this.y - y); }

    public float dot(ImmutableVector2f v)
    { return x * v.x() + y * v.y(); }

    public float angle(ImmutableVector2f v)
    {
        float dot = x * v.x() + y * v.y();
        float det = x * v.y() - y * v.x();
        return (float) Math.atan2(det, dot);
    }

    public float length()
    { return (float) Math.sqrt((x * x) + (y * y)); }

    /* (non-Javadoc)
     * @see org.joml.api.ImmutableVector2f#lengthSquared()
     */
    public float lengthSquared()
    { return x * x + y * y; }

    /* (non-Javadoc)
     * @see org.joml.api.ImmutableVector2f#distance(float, float)
     */
    public float distance(float x, float y)
    {
        float dx = this.x - x;
        float dy = this.y - y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /* (non-Javadoc)
     * @see org.joml.api.ImmutableVector2f#distanceSquared(float, float)
     */
    public float distanceSquared(float x, float y)
    {
        float dx = this.x - x;
        float dy = this.y - y;
        return dx * dx + dy * dy;
    }

    /* (non-Javadoc)
     * @see org.joml.api.ImmutableVector2f#distance(org.joml.api.ImmutableVector2f)
     */
    public float distance(ImmutableVector2f v)
    { return distance(v.x(), v.y()); }

    /* (non-Javadoc)
     * @see org.joml.api.ImmutableVector2f#distanceSquared(org.joml.api.ImmutableVector2f)
     */
    public float distanceSquared(ImmutableVector2f v)
    { return distanceSquared(v.x(), v.y()); }

    /**
     * Normalize this vector.
     *
     * @return this
     */
    public ImmutableVector2f normalize()
    {
        float invLength = (float) (1.0 / Math.sqrt(x * x + y * y));
        return new ImmutableVector2f(x * invLength, y * invLength);
    }

    /**
     * Scale this vector to have the given length.
     *
     * @param length the desired length
     * @return this
     */
    public ImmutableVector2f normalize(float length)
    {
        float invLength = (float) (1.0 / Math.sqrt(x * x + y * y)) * length;
        return new ImmutableVector2f(x * invLength, y * invLength);
    }

    /**
     * Add <code>v</code> to this vector.
     *
     * @param v the vector to add
     * @return this
     */
    public ImmutableVector2f add(ImmutableVector2f v)
    { return new ImmutableVector2f(x + v.x(), y + v.y()); }

    public ImmutableVector2f add(float scalar)
    {
        return new ImmutableVector2f(x + scalar, y + scalar);
    }

    /**
     * Increment the components of this vector by the given values.
     *
     * @param x the x component to add
     * @param y the y component to add
     * @return this
     */
    public ImmutableVector2f add(float x, float y)
    { return new ImmutableVector2f(x + x, y + y); }

    /**
     * Negate this vector.
     *
     * @return this
     */
    public ImmutableVector2f negate()
    { return new ImmutableVector2f(-x, -y); }

    /**
     * Multiply the components of this vector by the given scalar.
     *
     * @param scalar the value to multiply this vector's components by
     * @return this
     */
    public ImmutableVector2f mul(float scalar)
    { return new ImmutableVector2f(x * scalar, y * scalar); }

    /**
     * Multiply the components of this Vector2f by the given scalar values and store the result in <code>this</code>.
     *
     * @param x the x component to multiply this vector by
     * @param y the y component to multiply this vector by
     * @return this
     */
    public ImmutableVector2f mul(float x, float y)
    { return new ImmutableVector2f(this.x * x, this.y * y); }

    /**
     * Multiply this Vector2f component-wise by another Vector2f.
     *
     * @param v the vector to multiply by
     * @return this
     */
    public ImmutableVector2f mul(ImmutableVector2f v)
    { return new ImmutableVector2f(x * v.x(), y * v.y()); }

    public ImmutableVector2f div(ImmutableVector2f v)
    { return new ImmutableVector2f(x / v.x(), y / v.y()); }

    public ImmutableVector2f div(float scalar)
    { return new ImmutableVector2f(x / scalar, y / scalar); }

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
    public ImmutableVector2f lerp(ImmutableVector2f other, float t)
    { return new ImmutableVector2f(x + (other.x() - x) * t, y + (other.y() - y) * t); }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(x);
        result = prime * result + Float.floatToIntBits(y);
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj) { return true; }
        if(obj == null) { return false; }
        if(getClass() != obj.getClass()) { return false; }
        ImmutableVector2f other = (ImmutableVector2f) obj;
        if(!epsilonEquals(x, other.x)) { return false; }
        if(!epsilonEquals(y, other.y)) { return false; }
        return true;
    }

    /**
     */
    @Override
    public String toString()
    {
        return "ImmutableVector2f{" +
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
    public ImmutableVector2f fma(ImmutableVector2f a, ImmutableVector2f b)
    { return new ImmutableVector2f(x + a.x() * b.x(), y + a.y() * b.y()); }

    /**
     * Add the component-wise multiplication of <code>a * b</code> to this vector.
     *
     * @param a the first multiplicand
     * @param b the second multiplicand
     * @return this
     */
    public ImmutableVector2f fma(float a, ImmutableVector2f b)
    { return new ImmutableVector2f(x + a * b.x(), y + a * b.y()); }
}
