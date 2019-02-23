package com.github.ezauton.core.localization.sensors;

import com.github.ezauton.core.utils.Stopwatch;

import java.util.concurrent.TimeUnit;

/**
 * A utility class used for encoder conversion
 */
public class Encoders {
    public static Encoder fromTachometer(Tachometer tachometer, Stopwatch stopwatch) {
        return new Encoder() {

            double position = 0;

            @Override
            public double getVelocity() {
                return tachometer.getVelocity();
            }

            @Override
            public double getPosition() {
                position += stopwatch.pop(TimeUnit.SECONDS) * tachometer.getVelocity();
                return position;
            }
        };
    }

    /**
     * Convert an Encoder into an TranslationalDistanceSensor
     *
     * @param feetPerUnit       The distance traveled given the encoder rotated 1 unit.
     *                          For example, if our encoder had 4096 units in a rotation (as many do), and one
     *                          rotation was a 1.5 feet of travel distance (as it would be for a ~6 in wheel), then this value is 1.5/4096.
     * @param fpsPerNativeSpeed Conversion factor from native units to distance per second
     * @param enc               Reference to encoder
     * @return An TranslationalDistanceSensor
     */
    public static TranslationalDistanceSensor toTranslationalDistanceSensor(double feetPerUnit, double fpsPerNativeSpeed, Encoder enc) {
        return new TranslationalDistanceSensor() {
            @Override
            public double getPosition() {
                return enc.getPosition() * feetPerUnit;
            }

            @Override
            public double getVelocity() {
                return enc.getVelocity() * fpsPerNativeSpeed;
            }
        };
    }
}
