package com.team2502.ezauton.recorder;

import java.io.Serializable;

public class Colour implements Serializable
{
    private final double r,g,b,opacity;

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
