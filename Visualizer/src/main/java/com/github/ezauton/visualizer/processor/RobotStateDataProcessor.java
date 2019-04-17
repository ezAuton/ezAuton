package com.github.ezauton.visualizer.processor;

import com.github.ezauton.recorder.base.RobotStateRecorder;
import com.github.ezauton.recorder.base.frame.RobotStateFrame;
import com.github.ezauton.visualizer.util.DataProcessor;
import com.github.ezauton.visualizer.util.Environment;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RobotStateDataProcessor implements DataProcessor {
    private final RobotStateRecorder robotRec;
    private final List<RobotStateFrame> dataFrames;

    private Rectangle robot;
    private Circle posCircle;
    private Label headingLabel, posLabel, velocityLabel;
    private double spatialScaleFactorX;
    private double spatialScaleFactorY;
    private double originYPx;
    private double originXPx;
    private int robotLengthPx;
    private int robotWidthPx;

    public RobotStateDataProcessor(RobotStateRecorder robotRec) {
        this.robotRec = robotRec;
        dataFrames = robotRec.getDataFrames();
    }


    private double getX(double feetX) {
        return feetX * spatialScaleFactorX + originXPx;
    }

    private double getY(double feetY) {
        return -feetY * spatialScaleFactorY + originYPx;
    }

    @Override
    public void initEnvironment(Environment environment) {
        this.spatialScaleFactorX = environment.getScaleFactorX();
        this.spatialScaleFactorY = environment.getScaleFactorY();

        this.originXPx = environment.getOrigin().get(0);
        this.originYPx = environment.getOrigin().get(1);

        // box for robot
        robotWidthPx = (int) (dataFrames.get(0).getRobotWidth() * spatialScaleFactorX);
        robotLengthPx = (int) (dataFrames.get(0).getRobotLength() * spatialScaleFactorY);
        robot = new Rectangle(0, 0, robotWidthPx, robotLengthPx);
        posCircle = new Circle(3, Paint.valueOf("white"));
        posCircle.setStroke(Paint.valueOf("black"));


        // heading info
        headingLabel = new Label("0 radians");

        // x y loc
        posLabel = new Label("(0, 0)");

        velocityLabel = new Label("(0, 0)");

        robot.setFill(Paint.valueOf("cyan"));
        robot.setStroke(Paint.valueOf("black"));
        environment.getFieldAnchorPane().getChildren().add(0, robot);
        environment.getFieldAnchorPane().getChildren().add(posCircle);

        GridPane dataGridPane = environment.getDataGridPane(robotRec.getName());
        dataGridPane.addRow(0, new Label("Heading: "), headingLabel);
        dataGridPane.addRow(1, new Label("Position: "), posLabel);
        dataGridPane.addRow(2, new Label("Velocity: "), velocityLabel);
    }

    @Override
    public Map<Double, List<KeyValue>> generateKeyValues(Interpolator interpolator) {
        Map<Double, List<KeyValue>> ret = new HashMap<>();

        Rotate robotRotation = new Rotate();
        robot.getTransforms().add(robotRotation);

        for (RobotStateFrame frame : robotRec.getDataFrames()) {
            List<KeyValue> keyValues = new ArrayList<>();
            double x, y, heading;

            if (frame.getPos() != null) {
                x = getX(frame.getPos().get(0));
                y = getY(frame.getPos().get(1));

                heading = frame.getHeading();

                double robotX = x - robotWidthPx / 2D;
                double robotY = y - robotLengthPx;

                keyValues.add(new KeyValue(robot.xProperty(), robotX, interpolator));
                keyValues.add(new KeyValue(robot.yProperty(), robotY, interpolator));
//                keyValues.add(new KeyValue(robot.rotateProperty(), -Math.toDegrees(heading), interpolator));
                keyValues.add(new KeyValue(robotRotation.angleProperty(), -Math.toDegrees(heading), interpolator));
                keyValues.add(new KeyValue(robotRotation.pivotXProperty(), x, interpolator));
                keyValues.add(new KeyValue(robotRotation.pivotYProperty(), y, interpolator));
                keyValues.add(new KeyValue(robot.visibleProperty(), true, interpolator));
                keyValues.add(new KeyValue(posCircle.centerXProperty(), x, interpolator));
                keyValues.add(new KeyValue(posCircle.centerYProperty(), y, interpolator));
                keyValues.add(new KeyValue(headingLabel.textProperty(), String.format("%.02f radians", heading)));
                keyValues.add(new KeyValue(posLabel.textProperty(), String.format("(%.02f, %.02f)", frame.getPos().get(0), frame.getPos().get(1))));
                keyValues.add(new KeyValue(velocityLabel.textProperty(), String.format("(%.02f, %.02f)", frame.getRobotVelocity().get(0), frame.getRobotVelocity().get(1))));
            } else {
                keyValues.add(new KeyValue(robot.visibleProperty(), false, interpolator));
            }

            ret.put(frame.getTime(),
                    keyValues);
        }

        return ret;
    }
}
