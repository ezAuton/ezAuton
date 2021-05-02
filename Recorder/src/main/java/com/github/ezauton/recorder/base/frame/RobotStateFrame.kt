package com.github.ezauton.recorder.base.frame

import com.github.ezauton.conversion.ScalarVector
import kotlinx.serialization.Serializable

/**
 * An immutable object for storing the state of the robot at a particular point in time.
 *
 *
 * Generic drivetrain format
 */
@Serializable
open class RobotStateFrame(val pos: ScalarVector, val heading: Double = 0.0, val robotWidth: Double = 0.0, val robotLength: Double = 0.0, val robotVelocity: ScalarVector, val time: Double)
