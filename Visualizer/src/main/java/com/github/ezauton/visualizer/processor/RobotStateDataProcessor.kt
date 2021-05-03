package com.github.ezauton.visualizer.processor

import com.github.ezauton.recorder.base.RobotStateRecording
import com.github.ezauton.recorder.base.frame.RobotStateFrame
import com.github.ezauton.visualizer.util.DataProcessor
import com.github.ezauton.visualizer.util.Environment
import javafx.animation.Interpolator
import javafx.animation.KeyValue
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.paint.Paint
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Rotate

class RobotStateDataProcessor(private val robotRec: RobotStateRecording) : DataProcessor {
  private val dataFrames: List<RobotStateFrame> = robotRec.frames
  private lateinit var robot: Rectangle
  private lateinit var posCircle: Circle
  private lateinit var headingLabel: Label
  private lateinit var posLabel: Label
  private lateinit var velocityLabel: Label
  private var spatialScaleFactorX = 0.0
  private var spatialScaleFactorY = 0.0
  private var originYPx = 0.0
  private var originXPx = 0.0
  private var robotLengthPx = 0.0
  private var robotWidthPx = 0.0

  private fun getX(feetX: Double): Double {
    return feetX * spatialScaleFactorX + originXPx
  }

  private fun getY(feetY: Double): Double {
    return -feetY * spatialScaleFactorY + originYPx
  }

  override fun initEnvironment(environment: Environment) {
    spatialScaleFactorX = environment.scaleFactorX
    spatialScaleFactorY = environment.scaleFactorY
    originXPx = environment.origin[0]
    originYPx = environment.origin[1]

    // box for robot
    robotWidthPx = dataFrames[0].robotWidth * spatialScaleFactorX
    robotLengthPx = dataFrames[0].robotLength * spatialScaleFactorY
    robot = Rectangle(0.0, 0.0, robotWidthPx, robotLengthPx)
    posCircle = Circle(3.0, Paint.valueOf("white"))
    posCircle.stroke = Paint.valueOf("black")


    // heading info
    headingLabel = Label("0 radians")

    // x y loc
    posLabel = Label("(0, 0)")
    velocityLabel = Label("(0, 0)")
    robot.fill = Paint.valueOf("cyan")
    robot.stroke = Paint.valueOf("black")
    environment.fieldAnchorPane.children.add(0, robot)
    environment.fieldAnchorPane.children.add(posCircle)
    val dataGridPane: GridPane = environment.getDataGridPane(robotRec.name)
    dataGridPane.addRow(0, Label("Heading: "), headingLabel)
    dataGridPane.addRow(1, Label("Position: "), posLabel)
    dataGridPane.addRow(2, Label("Velocity: "), velocityLabel)
  }

  override fun generateKeyValues(interpolator: Interpolator): Map<Double, List<KeyValue>> {
    val ret: MutableMap<Double, MutableList<KeyValue>> = HashMap()
    val robotRotation = Rotate()
    robot.transforms.add(robotRotation)
    for (frame in robotRec.frames) {
      val keyValues: MutableList<KeyValue> = ArrayList()
      val x: Double = getX(frame.pos[0])
      val y: Double = getY(frame.pos[1])
      val heading: Double = frame.heading
      val robotX = x - robotWidthPx / 2.0
      val robotY = y - robotLengthPx
      keyValues.add(KeyValue(robot.xProperty(), robotX, interpolator))
      keyValues.add(KeyValue(robot.yProperty(), robotY, interpolator))
      //                keyValues.add(new KeyValue(robot.rotateProperty(), -Math.toDegrees(heading), interpolator));
      keyValues.add(KeyValue(robotRotation.angleProperty(), -Math.toDegrees(heading), interpolator))
      keyValues.add(KeyValue(robotRotation.pivotXProperty(), x, interpolator))
      keyValues.add(KeyValue(robotRotation.pivotYProperty(), y, interpolator))
      keyValues.add(KeyValue(robot.visibleProperty(), true, interpolator))
      keyValues.add(KeyValue(posCircle.centerXProperty(), x, interpolator))
      keyValues.add(KeyValue(posCircle.centerYProperty(), y, interpolator))
      keyValues.add(KeyValue(headingLabel.textProperty(), String.format("%.02f radians", heading)))
      keyValues.add(KeyValue(posLabel.textProperty(), String.format("(%.02f, %.02f)", frame.pos[0], frame.pos[1])))
      keyValues.add(KeyValue(velocityLabel.textProperty(), String.format("(%.02f, %.02f)", frame.robotVelocity[0], frame.robotVelocity[1])))
      ret[frame.time] = keyValues
    }
    return ret
  }

}
