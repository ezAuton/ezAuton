package org.github.ezauton.ezauton.visualizer;

import org.github.ezauton.ezauton.utils.MathUtils;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

enum StartPos
{
    LEFT(33D / 443D),
    CENTER(206D / 443D),
    RIGHT(358D / 443D);

    private final double proportion;

    StartPos(double proportion)
    {
        this.proportion = proportion;
    }

    /**
     * Turn a string into a {@link StartPos}
     *
     * @param str A string (like "center" or "banana")
     * @return An instance of startpos if possible (e.g {@link StartPos#CENTER}) or null if not possible (e.g banana becomes null)
     */
    public static StartPos fromString(String str)
    {
        if(str.trim().equalsIgnoreCase("left"))
        {
            return LEFT;
        }
        else if(str.trim().equalsIgnoreCase("right"))
        {
            return RIGHT;
        }
        else if(str.trim().equalsIgnoreCase("center"))
        {
            return CENTER;
        }
        return null;
    }

    public double getXPos(double windowWidth)
    {
        return windowWidth * proportion;
    }

    @Override
    public String toString()
    {
        return this.name();
    }
}

public class Controller implements Initializable
{
    private final ConfigManager configManager;
    @FXML
    public Circle constantCurvature;

    @FXML
    public Line constantCurvatureLine;

    @FXML
    public Line currentPathLine;
    @FXML
    public Circle closestPoint;
    /**
     * The blue rectangle that represents the robot
     */
    @FXML
    Rectangle robot;
    @FXML
    Circle goalPoint;
    @FXML
    AnchorPane backdrop;
    @FXML
    private Circle robotPoint;
    @FXML
    private Label dCP;
    @FXML
    private Label lookahead;
    @FXML
    private Slider rateSlider;
    @FXML
    private Label timeElapsed;
    @FXML
    private ChoiceBox<StartPos> posChooser;
    @FXML
    private ChoiceBox<File> fileChooser;
    /**
     * The {@code n x 5} 2D array that represents where the robot went
     */
    private double[][] robotTraj;

    /**
     * The {@code n x 2} @D array that represents where the robot was told to go
     * <br>
     * This comes from {@link com.team2502.robot2018.command.autonomous.ingredients.PathConfig}
     */
    private double[][] waypoints;

    /**
     * The path that shows you where the robot went
     */
    @FXML
    private Path robotPath;

    /**
     * The path that shows you where the robot was told to go
     */
    @FXML
    private Path waypointPath;

    /**
     * The coordinate (0, 0) is in the top left corner of the screen. Since driving forwards = up, this is bad.
     * The coordinate pair (originX, originY) dictates the absolute starting position of everything.
     * <p>
     * This way, we can make originY non zero allowing us to actually see the path
     */
    private double originX;
    private double originY;

    /**
     * By default, the {@link Controller#robot} is 2-3 pixels tall. This is much too small to learn anything.
     * This scales everything up so that everything is still proportional to each other but you can at least see it
     */
    private double spatialScaleFactor;

    private double initRobotHeight;
    private double initRobotWidth;
    private Timeline timeline;
    private ChangeListener<Number> listener;

    /**
     * Read the points from the CSV and put them into {@link Controller#robotTraj} and {@link Controller#waypoints} as needed
     */
    public Controller()
    {
        // Read the config file in the resources folder and initialize values appropriately
        // FIXME Hardcoded path >:(
        configManager = new ConfigManager("/home/ritikm/Robotics/ezAuton/Visualizer/src/main/resources/org/github/ezauton/ezauton/visualizer/config");
//        configManager = new ConfigManager(getClass().getResource("config").toExternalForm());
        configManager.load();

    }

    private static void printWaypointsNicely(double[][] waypoints)
    {
        System.out.println("time, x, y");
        for(double[] row : waypoints)
        {
            System.out.println(row[0] + ", " + row[1] + ", " + row[2]);
        }
    }

    private static List<File> getAllFilesInDirectory(String dir)
    {
        List<File> files = new ArrayList<>();
        File folder = new File(dir);
        File[] listOfFiles = folder.listFiles();

        try
        {
            for(int i = 0; i < Objects.requireNonNull(listOfFiles).length; i++)
            {
                if(listOfFiles[i].isFile())
                {
                    files.add(listOfFiles[i]);
                }
            }
            return files;
        }
        catch (NullPointerException e)
        {
            folder.mkdir();
            return new ArrayList<>();
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        backdrop.heightProperty().addListener((heightProp, oldHeight, newHeight) -> spatialScaleFactor = newHeight.doubleValue() / 30.0156D
                                             );

        backdrop.widthProperty().addListener((widthProp, oldWidth, newWidth) ->
                                             {
                                                 try
                                                 {
                                                     originX = posChooser.getValue().getXPos(newWidth.doubleValue());
                                                 }
                                                 catch(NullPointerException e)
                                                 {
                                                     // this is ok
                                                 }
                                             }
                                            );


        // Make sure the circle always stays with the robot
        robotPoint.centerXProperty().bind(robot.xProperty().add(robot.widthProperty().divide(2)));
        robotPoint.centerYProperty().bind(robot.yProperty().add(robot.heightProperty().divide(2)));

        backdrop.setOnMouseClicked(((e) -> {
            animateSquareKeyframe(e);
            backdrop.setOnMouseClicked((j) -> {});
        }));

        List<File> listOfCSVs = getAllFilesInDirectory(configManager.getString("csvPath"));

        fileChooser.getItems().addAll(listOfCSVs);
        fileChooser.valueProperty().addListener((selectedProp, oldSelected, newSelected) -> {
            try
            {
                loadCSV(newSelected);
                animateSquareKeyframe(null);
            }
            catch(IOException e1)
            {
                System.out.println("Try the following: ");
                System.out.println("1. Create a directory called outPaths in the root folder of the RobotCode2018 project");
                System.out.println("2. Run the unit tests for RobotCode2018 on this computer");
                System.out.println("Then it should work.");
            }

        });


        posChooser.getItems().addAll(StartPos.values());
        posChooser.valueProperty().addListener((selectedProp, oldSelected, newSelected) -> {
            originX = posChooser.getValue().getXPos(backdrop.getWidth());
            animateSquareKeyframe(null);
        });


        initRobotWidth = robot.getWidth();
        initRobotHeight = robot.getHeight();
    }

    private double getX(double ppX)
    {
        return ppX * spatialScaleFactor + originX + robot.getWidth() / 2;
    }

    private double getY(double ppY)
    {
        return -ppY * spatialScaleFactor + originY + robot.getHeight() / 2;
    }

    /**
     * Animate the robot following the path
     *
     * @param event This exists in case you want to add this as an onClickListener or something like that. Not used.
     */
    @FXML
    private void animateSquareKeyframe(Event event)
    {
        if(fileChooser.getValue() == null || posChooser.getValue() == null)
        {
            System.out.println("Please select a file and position!");
            return;
        }
        clear();
        // Animation works by interpolating key values between key frames
        // We store all our keyframes in this handy dandy list
        List<KeyFrame> keyFrames = new ArrayList<>();

        // Scale our robot appropriately
        robot.setWidth(initRobotWidth * spatialScaleFactor);
        robot.setHeight(initRobotHeight * spatialScaleFactor);


        originY = backdrop.getHeight() - robot.getHeight();


        // Add our first keyframe
        keyFrames.add(new KeyFrame(Duration.ZERO, new KeyValue(robot.xProperty(), originX),
                                   new KeyValue(robot.yProperty(), originY)));

        // Center the path on the robot
        double pathOffsetX = robot.getWidth() / 2;
        double pathOffsetY = robot.getHeight() / 2;

        double[] waypointInit = waypoints[0];

        double initX = waypointInit[0] * spatialScaleFactor + originX;
        double initY = -waypointInit[1] * spatialScaleFactor + originY;

        MoveTo initialOffset = new MoveTo(originX + pathOffsetX, originY + pathOffsetY);

        robotPath.getElements().add(initialOffset);

        waypointPath.getElements().add(new MoveTo(initX + pathOffsetX, initY + pathOffsetX));

        // Draw the path -- where our robot was told to go
        for(int i = 0; i < waypoints.length; i++)
        {
            if(i == 0)
            {
                continue;
            }
            double[] waypoint = waypoints[i];
            double x = waypoint[0] * spatialScaleFactor + originX;

            // We need this negative since positive y is downwards in JavaFX
            double y = -waypoint[1] * spatialScaleFactor + originY;

            LineTo lineTo = new LineTo(x + pathOffsetX, y + pathOffsetY);
            waypointPath.getElements().add(lineTo);
        }

        // Draw our drive path -- where our robot actually went
        for(int i = 1; i < robotTraj.length; i++)
        {
            // Get this waypoint and the next waypoint
            double[] waypoint = robotTraj[i - 1];

            double gpX = getX(waypoint[5]);
            double gpY = getY(waypoint[6]);

            double circleOnRadius = Math.abs(waypoint[7] * spatialScaleFactor);
            double circleOnX = getX(waypoint[8]);
            double circleOnY = getY(waypoint[9]);

            double closestPointX = getX(waypoint[11]);
            double closestPointY = getY(waypoint[12]);

            int pathOnI = (int) waypoint[10];

            double lineSegmentXI = getX(waypoints[pathOnI][0]);
            double lineSegmentYI = getY(waypoints[pathOnI][1]);
            double lineSegmentXF = getX(waypoints[pathOnI + 1][0]);
            double lineSegmentYF = getY(waypoints[pathOnI + 1][1]);


            double targetAngle = 90 - waypoint[4] * 180 / Math.PI;

            // Figure out where our robot belongs

            double x = waypoint[1] * spatialScaleFactor + originX;

            // We need this negative since positive y is downwards in JavaFX
            double y = -waypoint[2] * spatialScaleFactor + originY;

            // Put all our keyvalues (robot pos, robot angle, lookahead) in a keyframe
            boolean useLine = circleOnRadius > 10000;
            double lineStartX;
            double lineStartY;
            double lineEndX;
            double lineEndY;

            double robotCenterX = x + robot.getWidth() / 2;
            double robotCenterY = y + robot.getHeight() / 2;
            double lineDX = robotCenterX - gpX;
            double lineDY = robotCenterY - gpY;
            if(lineDX != 0)
            {
                double slope = lineDY / lineDX;
                MathUtils.Function line = (inp) -> slope * (inp - robotCenterX) + robotCenterY; // pointslope
                lineStartX = 0;
                lineStartY = line.get(lineStartX);
                lineEndX = backdrop.getWidth();
                lineEndY = line.get(lineEndX);
            }
            else
            {
                lineStartX = robotCenterX;
                lineStartY = 0;
                lineEndX = robotCenterX;
                lineEndY = backdrop.getHeight();
            }
            Interpolator interpolator;
            if(configManager.getDouble("rate") < 3D / 5D) // if we'll be playing at less than 30 fps
            {
                interpolator = Interpolator.EASE_BOTH;
            }
            else
            {
                interpolator = Interpolator.DISCRETE;
            }

            keyFrames.add(new KeyFrame(Duration.seconds(waypoint[0]),
                                       // Robot position
                                       new KeyValue(robot.xProperty(), x, interpolator),
                                       new KeyValue(robot.yProperty(), y, interpolator),
                                       new KeyValue(robot.rotateProperty(), targetAngle, interpolator),

                                       // Goalpoint position
                                       new KeyValue(goalPoint.centerXProperty(), gpX, interpolator),
                                       new KeyValue(goalPoint.centerYProperty(), gpY, interpolator),

                                       // Lookahead text
                                       new KeyValue(lookahead.textProperty(), String.format("%.02f feet", waypoint[3])),

                                       // Curvature pos
                                       new KeyValue(constantCurvature.centerXProperty(), circleOnX, interpolator),
                                       new KeyValue(constantCurvature.centerYProperty(), circleOnY, interpolator),
                                       new KeyValue(constantCurvature.radiusProperty(), circleOnRadius, interpolator),

                                       // Whether to use the circle or the line
                                       new KeyValue(constantCurvature.visibleProperty(), !useLine, interpolator),
                                       new KeyValue(constantCurvatureLine.visibleProperty(), useLine, interpolator),

                                       // Line position
                                       new KeyValue(constantCurvatureLine.startXProperty(), lineStartX, interpolator),
                                       new KeyValue(constantCurvatureLine.startYProperty(), lineStartY, interpolator),
                                       new KeyValue(constantCurvatureLine.endXProperty(), lineEndX, interpolator),
                                       new KeyValue(constantCurvatureLine.endYProperty(), lineEndY, interpolator),

                                       new KeyValue(currentPathLine.startXProperty(), lineSegmentXI, interpolator),
                                       new KeyValue(currentPathLine.startYProperty(), lineSegmentYI, interpolator),
                                       new KeyValue(currentPathLine.endXProperty(), lineSegmentXF, interpolator),
                                       new KeyValue(currentPathLine.endYProperty(), lineSegmentYF, interpolator),

                                       new KeyValue(closestPoint.centerXProperty(), closestPointX, interpolator),
                                       new KeyValue(closestPoint.centerYProperty(), closestPointY, interpolator),
                                       new KeyValue(timeElapsed.textProperty(), String.format("%.02f seconds", waypoint[0])),
                                       new KeyValue(dCP.textProperty(), String.format("%.02f feet", waypoint[13]))
            ));

            // Add our position information to the translucent grey path that shows where our robot went
            robotPath.getElements().add(new LineTo(x + pathOffsetX, y + pathOffsetY));
        }

        // Create the animation

        if(listener != null)
        {
            rateSlider.valueProperty().removeListener(listener);
        }

        if(timeline != null)
        {
            timeline.pause();
        }

        timeline = new Timeline();

        listener = (observable, oldValue, newValue) -> {
            double value = newValue.doubleValue();
            actOnTimeline(timeline, value);
        };

        rateSlider.valueProperty().addListener(listener);

        // Loop it forever
        timeline.setCycleCount(Timeline.INDEFINITE);

        // When the animation ends, the robot teleports from the end to the beginning instead of driving backwards
        timeline.setAutoReverse(false);

        // Add our keyframes to the animation
        keyFrames.forEach((KeyFrame kf) -> timeline.getKeyFrames().add(kf));

        // Play it
        timeline.playFromStart();
        actOnTimeline(timeline,rateSlider.valueProperty().doubleValue());
    }

    private void actOnTimeline(Timeline timeline, double value)
    {
        if(MathUtils.epsilonEquals(0, value))
        {
            timeline.pause();
        }
        else
        {
            timeline.play();
            timeline.setRate(value);
        }
    }

    private void clear()
    {
        robotPath.getElements().clear();
        waypointPath.getElements().clear();
    }

    private void loadCSV(File file) throws IOException
    {
        List<String> lines = Files.readAllLines(file.toPath());
        // Find out how many waypoints from PathConfig there are
        int numDefinedWaypoints = Integer.valueOf(lines.get(0));

        // Initialize the array for waypoints
        waypoints = new double[numDefinedWaypoints][2];

        // Remember that the waypoints begin on the row at the second index
        int startOfWaypoints = 2;

        // Knowing the total amount of rows and rows for PathConfig waypoints, we make a new array for the robot's movement
        robotTraj = new double[lines.size() - startOfWaypoints - numDefinedWaypoints + 1][lines.get(lines.size() - 1).split(", ").length];
        int i = startOfWaypoints; // 0th row has num waypoints; 1st has column headers for humans

        // Process the PathConfig waypoints
        for(; i < numDefinedWaypoints + 2; i++)
        {
            String row = lines.get(i);
            String[] data = row.split(", ");

            for(int j = 0; j < data.length; j++)
            {
                waypoints[i - startOfWaypoints][j] = Double.valueOf(data[j]);
            }
        }

        // Remember where the robot movement data begins
        int rowsForRobotMovement = startOfWaypoints + numDefinedWaypoints;
        i++; // Skip header row

        // Fill robotTraj
        for(; i < lines.size(); i++)
        {
            String row = lines.get(i);
            String[] data = row.split(", ");

            for(int j = 0; j < data.length; j++)
            {
                robotTraj[i - rowsForRobotMovement][j] = Double.valueOf(data[j]);
            }
        }
    }
}
