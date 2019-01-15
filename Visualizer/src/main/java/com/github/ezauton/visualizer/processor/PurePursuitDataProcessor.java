package com.github.ezauton.visualizer.processor;

import com.github.ezauton.core.pathplanning.IPathSegment;
import com.github.ezauton.visualizer.util.IDataProcessor;
import com.github.ezauton.visualizer.util.IEnvironment;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import com.github.ezauton.recorder.base.frame.PurePursuitFrame;
import com.github.ezauton.recorder.base.PurePursuitRecorder;
import com.github.ezauton.core.trajectory.geometry.ImmutableVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurePursuitDataProcessor implements IDataProcessor
{

    private final PurePursuitRecorder ppRec;
    private final Circle goalPoint;
    private final Circle closestPoint;
    private final Path waypointPath;
    private final Label lookaheadLabel;
    private final Label dcpLabel;
    private final Label segmentIndexLabel;
    private final Line currentSegmentLine;
    private double spatialScaleFactorX;
    private double spatialScaleFactorY;
    private double originXPx;
    private double originYPx;

    public PurePursuitDataProcessor(PurePursuitRecorder ppRec)
    {
        this.ppRec = ppRec;

        goalPoint = new Circle(3, Paint.valueOf("red"));
        closestPoint = new Circle(3, Paint.valueOf("lawngreen"));

        waypointPath = new Path();

        currentSegmentLine = new Line(0, 0, 0, 0);

        lookaheadLabel = new Label("0 feet");
        dcpLabel = new Label("0 feet");
        segmentIndexLabel = new Label("0");
    }


    private double getX(double feetX)
    {
        return feetX * spatialScaleFactorX + originXPx;
    }

    private double getY(double feetY)
    {
        return -feetY * spatialScaleFactorY + originYPx;
    }

    private ImmutableVector toPixels(ImmutableVector feet)
    {
        return new ImmutableVector(getX(feet.get(0)), getY(feet.get(1)));
    }

    @Override
    public void initEnvironment(IEnvironment environment)
    {
        this.spatialScaleFactorX = environment.getScaleFactorX();
        this.spatialScaleFactorY = environment.getScaleFactorY();

        this.originXPx = environment.getOrigin().get(0);
        this.originYPx = environment.getOrigin().get(1);

        AnchorPane anchorPane = environment.getFieldAnchorPane();

        waypointPath.getElements().add(new MoveTo((originXPx), (originYPx)));

        for(IPathSegment segment : ppRec.getPath().getPathSegments())
        {
            ImmutableVector to = segment.getTo();
            double x = to.get(0);
            double y = to.get(1);
            waypointPath.getElements().add(new LineTo(getX(x), getY(y)));
        }

        waypointPath.setStrokeWidth(1);
        waypointPath.setStroke(Paint.valueOf("black"));

        goalPoint.setCenterX((originXPx));
        goalPoint.setCenterX((originYPx));

        closestPoint.setCenterX((originXPx));
        closestPoint.setCenterX((originYPx));

        currentSegmentLine.setStrokeWidth(1);
        currentSegmentLine.setStroke(Paint.valueOf("orange"));

        anchorPane.getChildren().add(closestPoint);
        anchorPane.getChildren().add(goalPoint);
        anchorPane.getChildren().add(waypointPath);
        anchorPane.getChildren().add(currentSegmentLine);

        GridPane dataGridPane = environment.getDataGridPane(ppRec.getName());


        dataGridPane.addRow(0, new Label("Lookahead: "), lookaheadLabel);

        dataGridPane.addRow(1, new Label("Distance to Closest Point: "), dcpLabel);
        dataGridPane.addRow(2, new Label("Current segment number: "), segmentIndexLabel);
    }

    @Override
    public Map<Double, List<KeyValue>> generateKeyValues(Interpolator interpolator)
    {
        Map<Double, List<KeyValue>> ret = new HashMap<>();

        for(PurePursuitFrame frame : ppRec.getDataFrames())
        {
            List<KeyValue> keyValues = new ArrayList<>();
            double cpX, cpY, gpX, gpY;

            if(frame.getClosestPoint() != null)
            {
                cpX = getX(frame.getClosestPoint().get(0));
                cpY = getY(frame.getClosestPoint().get(1));

                IPathSegment currentSegment = ppRec.getPath().getPathSegments().get(frame.getCurrentSegmentIndex());

                double currentSegmentStartX = getX(currentSegment.getFrom().get(0));
                double currentSegmentStartY = getY(currentSegment.getFrom().get(1));
                double currentSegmentEndX = getX(currentSegment.getTo().get(0));
                double currentSegmentEndY = getY(currentSegment.getTo().get(1));


                keyValues.add(new KeyValue(closestPoint.centerXProperty(), cpX, interpolator));
                keyValues.add(new KeyValue(closestPoint.centerYProperty(), cpY, interpolator));
                keyValues.add(new KeyValue(closestPoint.visibleProperty(), true, interpolator));
                keyValues.add(new KeyValue(currentSegmentLine.startXProperty(), currentSegmentStartX, interpolator));
                keyValues.add(new KeyValue(currentSegmentLine.startYProperty(), currentSegmentStartY, interpolator));
                keyValues.add(new KeyValue(currentSegmentLine.endXProperty(), currentSegmentEndX, interpolator));
                keyValues.add(new KeyValue(currentSegmentLine.endYProperty(), currentSegmentEndY, interpolator));
                keyValues.add(new KeyValue(segmentIndexLabel.textProperty(), String.valueOf(frame.getCurrentSegmentIndex())));
            }
            else
            {
                keyValues.add(new KeyValue(closestPoint.visibleProperty(), false, interpolator));
            }

            if(frame.getGoalPoint() != null)
            {
                gpX = getX(frame.getGoalPoint().get(0));
                gpY = getY(frame.getGoalPoint().get(1));

                keyValues.add(new KeyValue(goalPoint.centerXProperty(), gpX, interpolator));
                keyValues.add(new KeyValue(goalPoint.centerYProperty(), gpY, interpolator));
                keyValues.add(new KeyValue(goalPoint.visibleProperty(), true, interpolator));
            }
            else
            {
                keyValues.add(new KeyValue(goalPoint.visibleProperty(), false, interpolator));
            }

            keyValues.add(new KeyValue(lookaheadLabel.textProperty(), String.format("%.02f feet", frame.getLookahead())));
            keyValues.add(new KeyValue(dcpLabel.textProperty(), String.format("%.02f feet", frame.getdCP())));

            ret.put(frame.getTime(),
                    keyValues);
        }

        return ret;
    }
}
