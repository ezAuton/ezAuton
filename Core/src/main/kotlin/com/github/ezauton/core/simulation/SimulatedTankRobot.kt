package com.github.ezauton.core.simulation

import com.github.ezauton.core.actuators.VelocityMotor
import com.github.ezauton.core.actuators.implementations.SimulatedMotor
import com.github.ezauton.core.localization.Updatable
import com.github.ezauton.core.localization.estimators.TankRobotEncoderEncoderEstimator
import com.github.ezauton.core.localization.sensors.Encoders
import com.github.ezauton.core.localization.sensors.TranslationalDistanceSensor
import com.github.ezauton.core.robot.TankRobotConstants
import com.github.ezauton.core.robot.implemented.TankRobotTransLocDrivable
import com.github.ezauton.core.utils.Clock
import com.github.ezauton.core.utils.Stopwatch

class SimulatedTankRobot
/**
 * @param lateralWheelDistance The lateral wheel distance between the wheels of the robot
 * @param clock The clock that the simulated tank robot is using
 * @param maxAccel The max acceleration of the motors
 * @param minVel The minimum velocity the robot can continuously drive at (i.e. the robot cannot drive at 0.0001 ft/s)
 */
(override val lateralWheelDistance: Double, clock: Clock, maxAccel: Double, minVel: Double, maxVel: Double) : TankRobotConstants, Updatable {

    private val left: SimulatedMotor
    private val right: SimulatedMotor

    private val stopwatch: Stopwatch
    val leftDistanceSensor: TranslationalDistanceSensor
    val rightDistanceSensor: TranslationalDistanceSensor
    /**
     * @return A location estimator which automatically updates
     */
    val defaultLocEstimator: TankRobotEncoderEncoderEstimator
    val defaultTransLocDriveable: TankRobotTransLocDrivable
    //    public StringBuilder log = new StringBuilder("t, v_l, v_r\n");
    private val toUpdate: Set<Updatable>

    val leftMotor: VelocityMotor
        get() = left

    val rightMotor: VelocityMotor
        get() = right

    init {
        stopwatch = Stopwatch(clock)
        stopwatch.init()

        left = SimulatedMotor(clock, maxAccel, minVel, maxVel, 1.0)
        leftDistanceSensor = Encoders.toTranslationalDistanceSensor(1.0, 1.0, left)

        right = SimulatedMotor(clock, maxAccel, minVel, maxVel, 1.0)
        rightDistanceSensor = Encoders.toTranslationalDistanceSensor(1.0, 1.0, right)

        toUpdate = setOf(left, right)

        this.defaultLocEstimator = TankRobotEncoderEncoderEstimator(leftDistanceSensor, rightDistanceSensor, this)
        this.defaultTransLocDriveable = TankRobotTransLocDrivable(left, right, defaultLocEstimator, defaultLocEstimator, this)
    }

    fun run(leftV: Double, rightV: Double) {
        left.runVelocity(leftV)
        right.runVelocity(rightV)
    }

    override fun update(): Boolean {
        //        long read = stopwatch.read(TimeUnit.SECONDS);
        //        log.append(read).append(", ").append(leftTDS.getVelocity()).append(", ").append(rightTDS.getVelocity()).append("\n");
        toUpdate.forEach { it.update() }
        defaultLocEstimator.update()
        return true
    }

    companion object {

        val NORM_DT = 0.02
    }
}
