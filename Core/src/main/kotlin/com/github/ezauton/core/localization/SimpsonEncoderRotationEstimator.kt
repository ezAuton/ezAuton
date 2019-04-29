package com.github.ezauton.core.localization

import com.github.ezauton.core.localization.sensors.VelocityEstimator
import com.github.ezauton.core.trajectory.geometry.ImmutableVector
import com.github.ezauton.core.utils.Clock
import com.github.ezauton.core.utils.MathUtils
import com.github.ezauton.core.utils.Stopwatch

import java.util.concurrent.TimeUnit

/**
 * Describes an Updateable object that can track the location and heading of the robot using a rotational device
 * which can record angle (i.e. gyro) and a device which can record translational distance (i.e., encoder).
 *
 *
 * This is different from EncoderRotationEstimator in that it uses Simpson's rule to acheive more accurae localization
 */
class SimpsonEncoderRotationEstimator
/**
 * Create an EncoderRotationEstimator
 *
 * @param rotationalLocationEstimator An object that can estimate our current heading
 * @param velocitySensor              An encoder or encoder-like object.
 */
(private val rotationalLocationEstimator: RotationalLocationEstimator, private val velocitySensor: VelocityEstimator, clock: Clock) : RotationalLocationEstimator, TranslationalLocationEstimator, Updateable {
    private val stopwatch: Stopwatch
    private var velocity: Double = 0.toDouble()
    private var dPosVec: ImmutableVector? = null
    private var positionVec: ImmutableVector? = null
    private var init = false

    /**
     * The velocity vector two iterations ago
     */
    private var vel2ago: TimeIndexedVelocityVec? = null

    /**
     * The velocity vector one iteration ago
     */
    private var vel1ago: TimeIndexedVelocityVec? = null

    init {
        this.stopwatch = Stopwatch(clock)
    }

    /**
     * Set the current position to <0, 0>, in effect resetting the location estimator
     */
    fun reset() //TODO: Reset heading
    {
        dPosVec = ImmutableVector(0, 0)
        positionVec = ImmutableVector(0, 0)
        init = true
        stopwatch.reset()
    }


    override fun estimateHeading(): Double {
        return rotationalLocationEstimator.estimateHeading()
    }

    /**
     * @return The current velocity vector of the robot in 2D space.
     */
    override fun estimateAbsoluteVelocity(): ImmutableVector {
        return MathUtils.Geometry.getVector(velocity, rotationalLocationEstimator.estimateHeading())
    }


    /**
     * @return The current location as estimated from the encoders
     */
    override fun estimateLocation(): ImmutableVector? {
        return positionVec
    }

    /**
     * Update the calculation for the current heading and position. Call this as frequently as possible to ensure optimal results
     *
     * @return True
     */
    override fun update(): Boolean {
        if (!init) {
            throw IllegalArgumentException("Must be initialized! (call reset())")
        }
        if (rotationalLocationEstimator is Updateable) {
            (rotationalLocationEstimator as Updateable).update()
        }
        velocity = velocitySensor.translationalVelocity
        val velVec = MathUtils.Geometry.getVector(velocity, rotationalLocationEstimator.estimateHeading())

        val currentTime = stopwatch.read(TimeUnit.MICROSECONDS) / 1e6

        if (vel1ago != null && vel2ago != null) {
            if (currentTime > vel1ago!!.time + epsilon) {
                dPosVec = ImmutableVector(0, 0)

                val xVelComponent = Parabola(
                        ImmutableVector(vel2ago!!.time, vel2ago!!.velVec.get(0)),
                        ImmutableVector(vel1ago!!.time, vel1ago!!.velVec.get(0)),
                        ImmutableVector(currentTime, velVec.get(0))
                )

                val yVelComponent = Parabola(
                        ImmutableVector(vel2ago!!.time, vel2ago!!.velVec.get(1)),
                        ImmutableVector(vel1ago!!.time, vel1ago!!.velVec.get(1)),
                        ImmutableVector(currentTime, velVec.get(1))
                )

                dPosVec = ImmutableVector(xVelComponent.integrate(), yVelComponent.integrate())

                if (!dPosVec!!.isFinite) {
                    System.err.println("vel2ago = " + vel2ago!!)
                    System.err.println("vel1ago = " + vel1ago!!)
                    System.err.println("currentTime = $currentTime")
                    throw RuntimeException("Collected multiple data points at the same time. Should be impossible. File an issue on the github ezauton")
                }
                positionVec = positionVec!!.add(dPosVec!!)

                vel2ago = TimeIndexedVelocityVec(currentTime, velVec)
                vel1ago = null
            }
        } else {
            if (vel1ago == null) {
                if (vel2ago == null || currentTime > vel2ago!!.time + epsilon) {
                    //                    System.out.println("vel2ago = " + vel2ago);
                    //                    System.out.println("currentTime = " + currentTime);
                    vel1ago = TimeIndexedVelocityVec(currentTime, velVec)
                }
            } else if (vel2ago == null) {
                vel2ago = vel1ago
                vel1ago = TimeIndexedVelocityVec(currentTime, velVec)
            }
        }
        return true //TODO: Return false sometimes?
    }

    private class TimeIndexedVelocityVec internal constructor(val time: Double, val velVec: ImmutableVector) {

        override fun toString(): String {
            val sb = StringBuilder("TimeIndexedVelocityVec{")
            sb.append("time=").append(time)
            sb.append(", velVec=").append(velVec)
            sb.append('}')
            return sb.toString()
        }
    }

    private class Parabola(point1: ImmutableVector, point2: ImmutableVector, point3: ImmutableVector) {
        private val a: Double
        private val b: Double
        private val c: Double

        private val lowerBound: Double
        private val upperBound: Double

        init {
            val x1 = point1.get(0)
            val y1 = point1.get(1)

            val x2 = point2.get(0)
            val y2 = point2.get(1)

            val x3 = point3.get(0)
            val y3 = point3.get(1)

            lowerBound = Math.min(x1, Math.min(x2, x3))
            upperBound = Math.max(x1, Math.max(x2, x3))

            val numerator = x1 * x1 * (y2 - y3) +
                    x3 * x3 * (y1 - y2) +
                    x2 * x2 * (y3 - y1)

            val denominator = (x1 - x2) * (x1 - x3) * (x2 - x3)

            this.b = numerator / denominator

            this.a = (y2 - y1 - this.b * (x2 - x1)) / (x2 * x2 - x1 * x1)

            this.c = y1 - a * x1 * x1 - b * x1
        }

        fun integrate(): Double {
            val antiderivative = { x -> a * x * x * x / 3 + b * x * x / 2 + c * x }
            return antiderivative.get(upperBound) - antiderivative.get(lowerBound)
        }
    }

    companion object {

        private val epsilon = 1e-3 // One millisecond; we can't reasonably expect our clock to have a resolution below 1 ms

        @JvmStatic
        fun main(args: Array<String>) {
            val parabola = Parabola(ImmutableVector(0, 2), ImmutableVector(4, 6), ImmutableVector(10, 2))
            println("parabola = " + parabola.integrate())
        }
    }
}
