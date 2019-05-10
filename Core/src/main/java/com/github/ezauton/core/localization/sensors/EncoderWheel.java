package com.github.ezauton.core.localization.sensors;

/**
 * The combination of an encoder and a wheel. This allows to calculate translational distance. An encoder without
 * wheel specifications can only calculate revolutions.
 */
//TODO: Perhaps redundant with Encoders#fixRegEncoder
public class EncoderWheel implements TranslationalDistanceSensor {

    private final RotationalDistanceSensor rotationalDistanceSensor;
    private final double wheelDiameter;
    private double multiplier = 1D;
    private double encoderPosMultiplied;
    private double encoderRawPos;


    /**
     * @param encoder       The encoder for measuring revolutions
     * @param wheelDiameter The diameter of the wheel with the encoder (recommended in ft)
     */
    public EncoderWheel(RotationalDistanceSensor rotationalDistanceSensor, double wheelDiameter) {
        this.rotationalDistanceSensor = rotationalDistanceSensor;
        this.wheelDiameter = wheelDiameter;
        encoderPosMultiplied = rotationalDistanceSensor.getPosition() * getMultiplier();
        encoderRawPos = rotationalDistanceSensor.getPosition();
    }

    public RotationalDistanceSensor getRotationalDistanceSensor() {
        return rotationalDistanceSensor;
    }

    public double getWheelDiameter() {
        return wheelDiameter;
    }

    public double getMultiplier() {
        return multiplier;
    }

    /**
     * @param multiplier If there are additional gear ratios to consider, this is the multiplier
     *                   (wheel rev / encoder rev)
     */
    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    /**
     * @return velocity (probably in ft/s)
     */
    @Override
    public double getVelocity() {
        return rotationalDistanceSensor.getVelocity() * Math.PI * wheelDiameter * getMultiplier(); // because minute to second
    }

    /**
     * @return position (probably in ft)
     */
    @Override
    public double getPosition() {
        double tempRawPos = rotationalDistanceSensor.getPosition();
        encoderPosMultiplied = (tempRawPos - encoderRawPos) * getMultiplier() + encoderPosMultiplied;
        encoderRawPos = tempRawPos;
        return encoderPosMultiplied * Math.PI * wheelDiameter;
    }
}
