package org.github.ezauton.ezauton.recorder.base;

import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.scene.control.Label;
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
    private double originXFt;
    private double originYFt;

    private Circle goalPoint;
    private Circle closestPoint;

    private Path waypointPath;
    private Label lookaheadLabel;
    private Label dcpLabel;

    public PurePursuitDataProcessor(PurePursuitRecorder ppRec)
    {
        this.ppRec = ppRec;

        goalPoint = new Circle(3, Paint.valueOf("red"));
        closestPoint = new Circle(3, Paint.valueOf("lawngreen"));

        waypointPath = new Path();

    }


    private double getX(double feetX)
    {
        return feetX * spatialScaleFactorX + originXFt;
    }

    private double getY(double feetY)
    {
        return -feetY * spatialScaleFactorY + originYFt;
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

        this.originXFt = environment.getOrigin().get(0);
        this.originYFt = environment.getOrigin().get(1);

        waypointPath.getElements().add(new MoveTo(getX(originXFt), getY(originYFt)));

        for(IPathSegment segment : ppRec.getPath().getPathSegments())
        {
            ImmutableVector to = segment.getTo();
            double x = to.get(0);
            double y = to.get(1);
            waypointPath.getElements().add(new LineTo(getX(x), getY(y)));
        }

        goalPoint.setCenterX(getX(originXFt));
        goalPoint.setCenterX(getY(originYFt));

        closestPoint.setCenterX(getX(originXFt));
        closestPoint.setCenterX(getY(originYFt));

        environment.getFieldAnchorPane().getChildren().add(closestPoint);
        environment.getFieldAnchorPane().getChildren().add(goalPoint);
        environment.getFieldAnchorPane().getChildren().add(waypointPath);

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
            System.out.println("frame = " + frame);
            double cpX, cpY, gpX, gpY;

            if(frame.getClosestPoint() != null)
            {
                cpX = frame.getClosestPoint().get(0);
                cpY = frame.getClosestPoint().get(1);

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
                gpX = frame.getGoalPoint().get(0);
                gpY = frame.getGoalPoint().get(1);

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
