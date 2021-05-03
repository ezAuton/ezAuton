package com.github.ezauton.visualizer.processor

import com.github.ezauton.conversion.ScalarVector
import com.github.ezauton.conversion.svec
import com.github.ezauton.recorder.base.PurePursuitRecording
import com.github.ezauton.visualizer.util.DataProcessor
import com.github.ezauton.visualizer.util.Environment
import javafx.animation.Interpolator
import javafx.animation.KeyValue
import javafx.scene.control.Label
import javafx.scene.paint.Paint
import javafx.scene.shape.*
import java.util.ArrayList

class PurePursuitDataProcessor(private val ppRec: PurePursuitRecording) : DataProcessor {
  private val goalPoint = Circle(3.0, Paint.valueOf("red"))
  private val closestPoint = Circle(3.0, Paint.valueOf("lawngreen"))
  private val waypointPath = Path()
  private val lookaheadLabel = Label("0 feet")
  private val dcpLabel = Label("0 feet")
  private val segmentIndexLabel = Label("0")
  private val currentSegmentLine = Line(0.0, 0.0, 0.0, 0.0)
  private var spatialScaleFactorX = 0.0
  private var spatialScaleFactorY = 0.0
  private var originXPx = 0.0
  private var originYPx = 0.0

  private fun getX(feetX: Double): Double {
    return feetX * spatialScaleFactorX + originXPx
  }

  private fun getY(feetY: Double): Double {
    return -feetY * spatialScaleFactorY + originYPx
  }

  private fun toPixels(feet: ScalarVector): ScalarVector {
    return svec(getX(feet[0]), getY(feet[1]))
  }

  override fun initEnvironment(environment: Environment) {
    spatialScaleFactorX = environment.scaleFactorX
    spatialScaleFactorY = environment.scaleFactorY
    originXPx = environment.origin[0]
    originYPx = environment.origin[1]
    val anchorPane = environment.fieldAnchorPane
    val startingPos: ScalarVector = ppRec.path.points[0]

    waypointPath.elements.add(MoveTo(getX(startingPos[0]), getY(startingPos[1])))
    for (to in ppRec.path.points.drop(1)) {
      val (x,y) = to
      waypointPath.elements.add(LineTo(getX(x), getY(y)))
    }
    waypointPath.strokeWidth = 1.0
    waypointPath.stroke = Paint.valueOf("black")
    goalPoint.centerX = originXPx
    goalPoint.centerX = originYPx
    closestPoint.centerX = originXPx
    closestPoint.centerX = originYPx
    currentSegmentLine.strokeWidth = 1.0
    currentSegmentLine.stroke = Paint.valueOf("orange")
    anchorPane.children.add(closestPoint)
    anchorPane.children.add(goalPoint)
    anchorPane.children.add(waypointPath)
    anchorPane.children.add(currentSegmentLine)
    val dataGridPane = environment.getDataGridPane(ppRec.name)
    dataGridPane.addRow(0, Label("Lookahead: "), lookaheadLabel)
    dataGridPane.addRow(1, Label("Distance to Closest Point: "), dcpLabel)
    dataGridPane.addRow(2, Label("Current segment number: "), segmentIndexLabel)
  }

  override fun generateKeyValues(interpolator: Interpolator): Map<Double, List<KeyValue>> {
    val ret: MutableMap<Double, MutableList<KeyValue>> = HashMap()
    for (frame in ppRec.frames) {
      val keyValues: MutableList<KeyValue> = ArrayList()
      val cpX: Double = getX(frame.closestPoint[0])
      val cpY: Double = getY(frame.closestPoint[1])
      val currentSegment = ppRec.path.segments[frame.currentSegmentIndex]
      val currentSegmentStartX = getX(currentSegment.from[0])
      val currentSegmentStartY = getY(currentSegment.from[1])
      val currentSegmentEndX = getX(currentSegment.to[0])
      val currentSegmentEndY = getY(currentSegment.to[1])
      keyValues.add(KeyValue(closestPoint.centerXProperty(), cpX, interpolator))
      keyValues.add(KeyValue(closestPoint.centerYProperty(), cpY, interpolator))
      keyValues.add(KeyValue(closestPoint.visibleProperty(), true, interpolator))
      keyValues.add(KeyValue(currentSegmentLine.startXProperty(), currentSegmentStartX, interpolator))
      keyValues.add(KeyValue(currentSegmentLine.startYProperty(), currentSegmentStartY, interpolator))
      keyValues.add(KeyValue(currentSegmentLine.endXProperty(), currentSegmentEndX, interpolator))
      keyValues.add(KeyValue(currentSegmentLine.endYProperty(), currentSegmentEndY, interpolator))
      keyValues.add(KeyValue(segmentIndexLabel.textProperty(), frame.currentSegmentIndex.toString()))
      val gpX: Double = getX(frame.goalPoint[0])
      val gpY: Double = getY(frame.goalPoint[1])
      keyValues.add(KeyValue(goalPoint.centerXProperty(), gpX, interpolator))
      keyValues.add(KeyValue(goalPoint.centerYProperty(), gpY, interpolator))
      keyValues.add(KeyValue(goalPoint.visibleProperty(), true, interpolator))
      keyValues.add(KeyValue(lookaheadLabel.textProperty(), String.format("%.02f feet", frame.lookahead)))
      keyValues.add(KeyValue(dcpLabel.textProperty(), java.lang.String.format("%.02f feet", frame.dCP)))
      ret[frame.time] = keyValues
    }
    return ret
  }

}
