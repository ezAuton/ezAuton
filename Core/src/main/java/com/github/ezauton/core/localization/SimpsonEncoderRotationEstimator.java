package com.github.ezauton.core.localization;

import com.github.ezauton.core.localization.sensors.VelocityEstimator;
import com.github.ezauton.core.trajectory.geometry.ImmutableVector;
import com.github.ezauton.core.utils.Clock;
import com.github.ezauton.core.utils.MathUtils;
import com.github.ezauton.core.utils.Stopwatch;

import java.util.concurrent.TimeUnit;

/**
 * Describes an Updateable object that can track the location and heading of the robot using a rotational device
 * which can record angle (i.e. gyro) and a device which can record translational distance (i.e., encoder).
 * <p>
 * This is different from EncoderRotationEstimator in that it uses Simpson's rule to acheive more accurae localization
 */
public final class SimpsonEncoderRotationEstimator implements RotationalLocationEstimator, TranslationalLocationEstimator, Updateable {

    private static final double epsilon = 1e-3; // One millisecond; we can't reasonably expect our clock to have a resolution below 1 ms
    private final RotationalLocationEstimator rotationalLocationEstimator;
    private final VelocityEstimator velocitySensor;
    private final Stopwatch stopwatch;
    private double velocity;
    private ImmutableVector dPosVec;
    private ImmutableVector positionVec;
    private boolean init = false;
    /**
     * The velocity vector two iterations ago
     */
    private TimeIndexedVelocityVec vel2ago;

    /**
     * The velocity vector one iteration ago
     */
    private TimeIndexedVelocityVec vel1ago;

    /**
     * Create an EncoderRotationEstimator
     *
     * @param rotationalLocationEstimator An object that can estimate our current heading
     * @param velocitySensor              An encoder or encoder-like object.
     */
    public SimpsonEncoderRotationEstimator(RotationalLocationEstimator rotationalLocationEstimator, VelocityEstimator velocitySensor, Clock clock) {
        this.rotationalLocationEstimator = rotationalLocationEstimator;
        this.velocitySensor = velocitySensor;
        this.stopwatch = new Stopwatch(clock);
    }

    public static void main(String[] args) {
        Parabola parabola = new Parabola(new ImmutableVector(0, 2), new ImmutableVector(4, 6), new ImmutableVector(10, 2));
        System.out.println("parabola = " + parabola.integrate());
    }

    /**
     * Set the current position to <0, 0>, in effect resetting the location estimator
     */
    public void reset() //TODO: Reset heading
    {
        dPosVec = new ImmutableVector(0, 0);
        positionVec = new ImmutableVector(0, 0);
        init = true;
        stopwatch.reset();
    }

    @Override
    public double estimateHeading() {
        return rotationalLocationEstimator.estimateHeading();
    }

    /**
     * @return The current velocity vector of the robot in 2D space.
     */
    @Override
    public ImmutableVector estimateAbsoluteVelocity() {
        return MathUtils.Geometry.getVector(velocity, rotationalLocationEstimator.estimateHeading());
    }

    /**
     * @return The current location as estimated from the encoders
     */
    @Override
    public ImmutableVector estimateLocation() {
        return positionVec;
    }

    /**
     * Update the calculation for the current heading and position. Call this as frequently as possible to ensure optimal results
     *
     * @return True
     */
    @Override
    public boolean update() {
        if (!init) {
            throw new IllegalArgumentException("Must be initialized! (call reset())");
        }
        if (rotationalLocationEstimator instanceof Updateable) {
            ((Updateable) rotationalLocationEstimator).update();
        }
        velocity = velocitySensor.getTranslationalVelocity();
        ImmutableVector velVec = MathUtils.Geometry.getVector(velocity, rotationalLocationEstimator.estimateHeading());

        double currentTime = stopwatch.read(TimeUnit.MICROSECONDS) / 1e6D;

        if (vel1ago != null && vel2ago != null) {
            if (currentTime > vel1ago.getTime() + epsilon) {
                dPosVec = new ImmutableVector(0, 0);

                Parabola xVelComponent = new Parabola(
                        new ImmutableVector(vel2ago.getTime(), vel2ago.getVelVec().get(0)),
                        new ImmutableVector(vel1ago.getTime(), vel1ago.getVelVec().get(0)),
                        new ImmutableVector(currentTime, velVec.get(0))
                );

                Parabola yVelComponent = new Parabola(
                        new ImmutableVector(vel2ago.getTime(), vel2ago.getVelVec().get(1)),
                        new ImmutableVector(vel1ago.getTime(), vel1ago.getVelVec().get(1)),
                        new ImmutableVector(currentTime, velVec.get(1))
                );

                dPosVec = new ImmutableVector(xVelComponent.integrate(), yVelComponent.integrate());

                if (!dPosVec.isFinite()) {
                    System.err.println("vel2ago = " + vel2ago);
                    System.err.println("vel1ago = " + vel1ago);
                    System.err.println("currentTime = " + currentTime);
                    throw new RuntimeException("Collected multiple data points at the same time. Should be impossible. File an issue on the github ezauton");
                }
                positionVec = positionVec.add(dPosVec);

                vel2ago = new TimeIndexedVelocityVec(currentTime, velVec);
                vel1ago = null;
            }
        } else {
            if (vel1ago == null) {
                if (vel2ago == null || currentTime > vel2ago.getTime() + epsilon) {
//                    System.out.println("vel2ago = " + vel2ago);
//                    System.out.println("currentTime = " + currentTime);
                    vel1ago = new TimeIndexedVelocityVec(currentTime, velVec);
                }
            } else if (vel2ago == null) {
                vel2ago = vel1ago;
                vel1ago = new TimeIndexedVelocityVec(currentTime, velVec);
            }
        }
        return true; //TODO: Return false sometimes?
    }

    private static class TimeIndexedVelocityVec {
        private final double time;
        private final ImmutableVector velVec;

        TimeIndexedVelocityVec(double time, ImmutableVector velVec) {
            this.time = time;
            this.velVec = velVec;
        }

        public double getTime() {
            return time;
        }

        public ImmutableVector getVelVec() {
            return velVec;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("TimeIndexedVelocityVec{");
            sb.append("time=").append(time);
            sb.append(", velVec=").append(velVec);
            sb.append('}');
            return sb.toString();
        }
    }

    private static class Parabola {
        private final double a;
        private final double b;
        private final double c;

        private final double lowerBound;
        private final double upperBound;

        public Parabola(ImmutableVector point1, ImmutableVector point2, ImmutableVector point3) {
            double x1 = point1.get(0);
            double y1 = point1.get(1);

            double x2 = point2.get(0);
            double y2 = point2.get(1);

            double x3 = point3.get(0);
            double y3 = point3.get(1);

            lowerBound = Math.min(x1, Math.min(x2, x3));
            upperBound = Math.max(x1, Math.max(x2, x3));

            double numerator = x1 * x1 * (y2 - y3) +
                    x3 * x3 * (y1 - y2) +
                    x2 * x2 * (y3 - y1);

            double denominator = (x1 - x2) * (x1 - x3) * (x2 - x3);

            this.b = numerator / denominator;

            this.a = (y2 - y1 - this.b * (x2 - x1)) / (x2 * x2 - x1 * x1);

            this.c = y1 - a * x1 * x1 - b * x1;
        }

        public double integrate() {
            MathUtils.Function antiderivative = (x) -> ((a * x * x * x) / 3 + (b * x * x) / 2 + (c * x));
            return antiderivative.get(upperBound) - antiderivative.get(lowerBound);
        }
    }
}
