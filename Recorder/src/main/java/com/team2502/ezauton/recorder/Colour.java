package com.team2502.ezauton.recorder;

import java.io.Serializable;

public class Colour implements Serializable
{
    private final double r,g,b,opacity;

    public static final Colour BLACK = new Colour(0,0,0,1);
    public static final Colour WHITE = new Colour(1,1,1,1);
    public static final Colour RED = new Colour(1,0,0,1);
    public static final Colour GREEN = new Colour(0,1,0,1);
    public static final Colour BLUE = new Colour(0,0,1,1);

    public Colour(double r, double g, double b, double opacity)
    {
        this.r = r;
        this.g = g;
        this.b = b;
        this.opacity = opacity;
    }

    public double getR()
    {
        return r;
    }

    public double getG()
    {
        return g;
    }

    public double getB()
    {
        return b;
    }

    public double getOpacity()
    {
        return opacity;
    }
}
