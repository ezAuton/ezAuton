package com.github.ezauton.core.localization.sensors;

/**
 * The combination of an encoder and a wheel. This allows to calculate translational distance. An encoder without
 * wheel specifications can only calculate revolutions.
 */
//TODO: Perhaps redundant with Encoders#fixRegEncoder
public class EncoderWheel implements TranslationalDistanceSensor {

    private final RotationalDistanceSensor rotationalDistanceSensor;
    private final double wheelDiameter;

    private double encoderPosMultiplied;
    private double encoderRawPos;

    private double distanceMultiplier = 1D;
    private double timeMultiplier = 1/60D; // Suggested default

    /**
     * @param rotationalDistanceSensor The encoder for measuring revolutions
     * @param wheelDiameter            The diameter of the wheel with the encoder (recommended in ft)
     * @param timeMultiplier           A scaling factor that accounts for the fact that the encoder may read ticks/100ms,
     *                                 but you care about velocity in ft/s.
     */
    public EncoderWheel(RotationalDistanceSensor rotationalDistanceSensor, double wheelDiameter, double timeMultiplier) {
        this.rotationalDistanceSensor = rotationalDistanceSensor;
        this.wheelDiameter = wheelDiameter;
        encoderPosMultiplied = rotationalDistanceSensor.getPosition() * getDistanceMultiplier();
        encoderRawPos = rotationalDistanceSensor.getPosition();
        this.timeMultiplier = timeMultiplier;
    }


    /**
     * @return velocity (probably in ft/s)
     */
    @Override
    public double getVelocity() {

        //  encoder revolutions     wheel revs       wheel circumference (ft)      1 minute
        //  -------------------  *  ------------  *  ------------------------- * --------------
        //  minute                  encoder revs         wheel revolution          60 seconds
        return rotationalDistanceSensor.getVelocity() * (Math.PI * wheelDiameter) * getDistanceMultiplier() * getTimeMultiplier(); // because minute to second
    }

    /**
     * @return position (probably in ft)
     */
    @Override
    public double getPosition() {
        double tempRawPos = rotationalDistanceSensor.getPosition();
        encoderPosMultiplied = (tempRawPos - encoderRawPos) * getDistanceMultiplier() + encoderPosMultiplied;
        encoderRawPos = tempRawPos;
        return encoderPosMultiplied * Math.PI * wheelDiameter;
    }

    public double getTimeMultiplier() {
        return timeMultiplier;
    }

    /**
     * If you are converting from RPM (if that's what the encoder tells you) to ft/s,
     * you need a time scale
     *
     * @param timeMultiplier
     */
    public void setTimeMultiplier(double timeMultiplier) {
        this.timeMultiplier = timeMultiplier;
    }

    public double getDistanceMultiplier() {
        return distanceMultiplier;
    }

    /**
     * @param distanceMultiplier If there are additional gear ratios to consider, this is the distanceMultiplier
     *                           (wheel rev / encoder rev)
     */
    public void setDistanceMultiplier(double distanceMultiplier) {
        this.distanceMultiplier = distanceMultiplier;
    }

    public RotationalDistanceSensor getRotationalDistanceSensor() {
        return rotationalDistanceSensor;
    }

    public double getWheelDiameter() {
        return wheelDiameter;
    }
}
