package org.github.ezauton.ezauton.recorder.base;

import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import org.github.ezauton.ezauton.pathplanning.IPathSegment;
import org.github.ezauton.ezauton.trajectory.geometry.ImmutableVector;
import org.github.ezauton.ezauton.visualizer.IDataProcessor;
import org.github.ezauton.ezauton.visualizer.IEnvironment;

import java.util.*;

public class PurePursuitDataProcessor implements IDataProcessor
{

    private final PurePursuitRecorder ppRec;
    private double spatialScaleFactorX;
    private double spatialScaleFactorY;
    private double originXPx;
    private double originYPx;

    private Circle goalPoint;
    private Circle closestPoint;

    private Path waypointPath;
    private Label lookaheadLabel;
    private Label dcpLabel;
    private double windowHeight;

    public PurePursuitDataProcessor(PurePursuitRecorder ppRec)
    {
        this.ppRec = ppRec;

        goalPoint = new Circle(3, Paint.valueOf("red"));
        closestPoint = new Circle(3, Paint.valueOf("lawngreen"));

        waypointPath = new Path();

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

        AnchorPane anchorPane = environment.getFieldAnchorPane();
        windowHeight = anchorPane.getHeight();

        this.originXPx = environment.getOrigin().get(0);
        this.originYPx = environment.getOrigin().get(1);

        waypointPath.getElements().add(new MoveTo((originXPx), (originYPx)));

        for(IPathSegment segment : ppRec.getPath().getPathSegments())
        {
            ImmutableVector to = segment.getTo();
            double x = to.get(0);
            double y = to.get(1);
            System.out.println("getX(x) = " + getX(x));
            System.out.println("getY(y) = " + getY(y));
            waypointPath.getElements().add(new LineTo(getX(x), getY(y)));
        }

        waypointPath.setStrokeWidth(1);
        waypointPath.setStroke(Paint.valueOf("black"));

        System.out.println("originXPx = " + originXPx);
        System.out.println("originYPx = " + originYPx);
        goalPoint.setCenterX((originXPx));
        goalPoint.setCenterX((originYPx));

        closestPoint.setCenterX((originXPx));
        closestPoint.setCenterX((originYPx));

        anchorPane.getChildren().add(closestPoint);
        anchorPane.getChildren().add(goalPoint);
        anchorPane.getChildren().add(waypointPath);

        GridPane dataGridPane = environment.getDataGridPane(ppRec.getName());

        lookaheadLabel = new Label("0 feet");
        dataGridPane.addRow(0, new Label("Lookahead: "), lookaheadLabel);


        dcpLabel = new Label("0 feet");
        dataGridPane.addRow(1, new Label("Distance to Closest Point: "), dcpLabel);
    }

    @Override
    public Map<Double, List<KeyValue>> forKeyFrame(Interpolator interpolator)
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

                keyValues.add(new KeyValue(closestPoint.centerXProperty(), cpX, interpolator));
                keyValues.add(new KeyValue(closestPoint.centerYProperty(), cpY, interpolator));
                keyValues.add(new KeyValue(closestPoint.visibleProperty(), true, interpolator));
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
