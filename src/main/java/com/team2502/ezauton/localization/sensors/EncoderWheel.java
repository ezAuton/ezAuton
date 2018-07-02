package com.team2502.ezauton.localization.sensors;

/**
 * The combination of an encoder and a wheel. This allows to calculate translational distance, as a mere encoder
 * can only calculate revolutions.
 */
public class EncoderWheel implements ITranslationalDistanceSensor
{

    private final IEncoder encoder;
    private final double wheelDiameter;
    private double multiplier = 1D;

    /**
     * @param encoder       The encoder for measuring revolutions
     * @param wheelDiameter The diameter of the wheel with the encoder (recommended in ft)
     */
    public EncoderWheel(IEncoder encoder, double wheelDiameter)
    {
        this.encoder = encoder;
        this.wheelDiameter = wheelDiameter;
    }

    public IEncoder getEncoder()
    {
        return encoder;
    }

    public double getWheelDiameter()
    {
        return wheelDiameter;
    }

    public double getMultiplier()
    {
        return multiplier;
    }

    /**
     * @param multiplier If there are additional gear ratios to consider, this is the multiplier
     *                   (wheel rev / encoder rev)
     */
    public void setMultiplier(double multiplier)
    {
        this.multiplier = multiplier;
    }

    /**
     * @return velocity (probably in ft/s)
     */
    @Override
    public double getVelocity()
    {
        return encoder.getVelocity() * Math.PI * wheelDiameter * getMultiplier(); // because minute to second
    }

    /**
     * @return position (probably in ft)
     */
    @Override
    public double getPosition()
    {
        double epos = encoder.getPosition();
        return epos * Math.PI * wheelDiameter * getMultiplier();
    }
}
