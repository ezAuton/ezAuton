package com.github.ezauton.core.localization

import com.github.ezauton.conversion.*
import com.github.ezauton.core.localization.sensors.VelocityEstimator
import com.github.ezauton.core.utils.Clock
import com.github.ezauton.core.utils.Stopwatch
import com.github.ezauton.core.utils.math.polarVector2D

typealias TimeIndexedVelocityVec = Pair<SIUnit<Time>, ConcreteVector<Velocity>>

/**
 * Describes an Updatable object that can track the location and heading of the robot using a rotational device
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
 * @param velocitySensor An encoder or encoder-like object.
 */
  (
  private val rotationalLocationEstimator: RotationalLocationEstimator,
  private val velocitySensor: VelocityEstimator,
  clock: Clock
) : RotationalLocationEstimator, TranslationalLocationEstimator, Updatable {

  private val stopwatch: Stopwatch = Stopwatch(clock)
  private var velocity: SIUnit<Velocity> = Double.NaN.mps
  private var dPosVec = ConcreteVector.empty<Distance>()
  private var positionVec = ConcreteVector.empty<Distance>()
  private var init = false

  /**
   * The velocity vector two iterations ago
   */
  private var vel2ago: TimeIndexedVelocityVec? = null

  /**
   * The velocity vector one iteration ago
   */
  private var vel1ago: TimeIndexedVelocityVec? = null

  /**
   * Set the current position to <0, 0>, in effect resetting the location estimator
   */
  fun reset() { // TODO: Reset heading
    dPosVec = cvec(0.0, 0.0)
    positionVec = cvec(0.0, 0.0)
    init = true
    stopwatch.reset()
  }

  override fun estimateHeading(): SIUnit<Angle> {
    return rotationalLocationEstimator.estimateHeading()
  }

  /**
   * @return The current velocity vector of the robot in 2D space.
   */
  override fun estimateAbsoluteVelocity() =
    polarVector2D(magnitude = velocity, angle = rotationalLocationEstimator.estimateHeading())

  /**
   * @return The current location as estimated from the encoders
   */
  override fun estimateLocation(): ConcreteVector<Distance> = positionVec

  /**
   * Update the calculation for the current heading and position. Call this as frequently as possible to ensure optimal results
   *
   * @return True
   */
  override fun update(): Boolean {
    if (!init) {
      throw IllegalArgumentException("Must be initialized! (call reset())")
    }
    if (rotationalLocationEstimator is Updatable) {
      (rotationalLocationEstimator as Updatable).update()
    }
    velocity = velocitySensor.translationalVelocity
    val velVec = polarVector2D(magnitude = velocity, angle = rotationalLocationEstimator.estimateHeading())

    val currentTime = stopwatch.read()

    if (vel1ago != null && vel2ago != null) {
      if (currentTime > vel1ago!!.first + epsilon.seconds) {
        dPosVec = cvec(0.0, 0.0)

        val xVelComponent = Parabola(
          scalarVec(vel2ago!!.time, vel2ago!!.velVec[0]),
          scalarVec(vel1ago!!.time, vel1ago!!.velVec[0]),
          scalarVec(currentTime, velVec[0])
        )

        val yVelComponent = Parabola(
          ScalarVector(vel2ago!!.time, vel2ago!!.velVec[1]),
          ScalarVector(vel1ago!!.time, vel1ago!!.velVec[1]),
          ScalarVector(currentTime, velVec[1])
        )

        dPosVec = ScalarVector(xVelComponent.integrate(), yVelComponent.integrate())

        if (!dPosVec.isFinite) {
          System.err.println("vel2ago = " + vel2ago!!)
          System.err.println("vel1ago = " + vel1ago!!)
          System.err.println("currentTime = $currentTime")
          throw RuntimeException("Collected multiple data points at the same time. Should be impossible. File an issue on the github ezauton")
        }
        positionVec += dPosVec

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
    return true // TODO: Return false sometimes?
  }

  private class Parabola<T : Any>(point1: ConcreteVector<T>, point2: ConcreteVector<T>, point3: ConcreteVector<T>) {
    private val a: Double
    private val b: Double
    private val c: Double

    private val lowerBound: SIUnit<T>
    private val upperBound: SIUnit<T>

    init {
      val x1 = point1[0]
      val y1 = point1[1]

      val x2 = point2[0]
      val y2 = point2[1]

      val x3 = point3[0]
      val y3 = point3[1]

      lowerBound = min(x1, min(x2, x3))
      upperBound = max(x1, max(x2, x3))

      val numerator =
        x1 * x1 * (y2 - y3) + x3 * x3 * (y1 - y2) +
            x2 * x2 * (y3 - y1)

      val denominator = (x1 - x2) * (x1 - x3) * (x2 - x3)

      this.b = numerator / denominator

      this.a = (y2 - y1 - this.b * (x2 - x1)) / (x2 * x2 - x1 * x1)

      this.c = y1 - a * x1 * x1 - b * x1
    }

    fun integrate(): Double {
      fun antiDerivative(x: Double) = a * x * x * x / 3 + b * x * x / 2 + c * x
      return antiDerivative(upperBound) - antiDerivative(lowerBound)
    }
  }

  companion object {

    private const val epsilon =
      1e-3 // One millisecond; we can't reasonably expect our clock to have a resolution below 1 ms

    @JvmStatic
    fun main(args: Array<String>) {
      val parabola = Parabola(scalarVec(0.0, 2.0), ScalarVector(4.0, 6.0), ScalarVector(10.0, 2.0))
      println("parabola = " + parabola.integrate())
    }
  }
}
