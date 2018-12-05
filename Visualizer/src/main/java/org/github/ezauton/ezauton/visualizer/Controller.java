package org.github.ezauton.ezauton.visualizer;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import org.github.ezauton.ezauton.recorder.ISubRecording;
import org.github.ezauton.ezauton.recorder.JsonUtils;
import org.github.ezauton.ezauton.recorder.Recording;
import org.github.ezauton.ezauton.trajectory.geometry.ImmutableVector;
import org.github.ezauton.ezauton.utils.MathUtils;
import org.github.ezauton.ezauton.visualizer.processor.factory.FactoryMap;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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
    public TabPane tabPane;

    @FXML
    AnchorPane backdrop;

    @FXML
    private Slider rateSlider;

    @FXML
    private Label timeElapsed;

    @FXML
    private ChoiceBox<StartPos> posChooser;

    @FXML
    private ChoiceBox<File> fileChooser;


    /**
     * The coordinate (0, 0) is in the top left corner of the screen. Since driving forwards = up, this is bad.
     * The coordinate pair (originX, originY) dictates the absolute starting position of everything.
     * <p>
     * This way, we can make originY non zero allowing us to actually see the path
     */
    private double originX;
    private double originY;

    /**
     * By default, the robot is 2-3 pixels tall. This is much too small to learn anything.
     * This scales everything up so that everything is still proportional to each other but you can at least see it
     */
    private double spatialScaleFactor;

    private double initRobotHeight;
    private double initRobotWidth;
    private Timeline timeline;
    private ChangeListener<Number> listener;

    private Recording currentRecording;

    /**
     */
    public Controller()
    {
        // Read the config file in the resources folder and initialize values appropriately
        // FIXME Hardcoded path >:(
        String homeDir = System.getProperty("user.home");
        java.nio.file.Path filePath = Paths.get(homeDir, ".ezauton","config","visualizer.config");

        try
        {
            Files.createDirectories(filePath.getParent());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        try
        {
            if(!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }


        configManager = new ConfigManager(filePath);
//        configManager = new ConfigManager(getClass().getResource("config").toExternalForm());
        configManager.load();

    }

    private static String getExtension(String fileName)
    {
        String ret = fileName.substring(fileName.lastIndexOf(".") + 1);
        System.out.println("ret = " + ret);
        return ret;
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
                if(listOfFiles[i].isFile() && getExtension(listOfFiles[i].getName()).equalsIgnoreCase("json"))
                {
                    files.add(listOfFiles[i]);
                }
            }
            return files;
        }
        catch(NullPointerException e)
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

        backdrop.setOnMouseClicked(((e) -> {
            animateSquareKeyframe(e);
            backdrop.setOnMouseClicked((j) -> {});
        }));

        List<File> listOfCSVs = getAllFilesInDirectory(Paths.get(System.getProperty("user.home"), ".ezauton").toString());
        fileChooser.getItems().addAll(listOfCSVs);
        fileChooser.valueProperty().addListener((selectedProp, oldSelected, newSelected) -> {
            try
            {
                loadRecording(newSelected);
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


//        initRobotWidth = robot.getWidth();
//        initRobotHeight = robot.getHeight();
    }

    private double getX(double ppX)
    {
        return 0;
//        return ppX * spatialScaleFactor + originX + robot.getWidth() / 2;
    }

    private double getY(double ppY)
    {
        return 0;
//        return -ppY * spatialScaleFactor + originY + robot.getHeight() / 2;
    }

    private void clear() {
        tabPane.getTabs().clear();
        backdrop.getChildren().clear();
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
        // Animation works by interpolating key values between key frames
        // We store all our keyframes in this handy dandy list
        List<KeyFrame> keyFrames = new ArrayList<>();

        // Scale our robot appropriately

        originY = backdrop.getHeight();


        Interpolator interpolator;
        if(configManager.getDouble("rate") < 3D / 5D) // if we'll be playing at less than 30 fps
        {
            interpolator = Interpolator.EASE_BOTH;
        }
        else
        {
            interpolator = Interpolator.DISCRETE;
        }

        // Clear everything
        clear();

        for(Map.Entry<String, ISubRecording> entry : currentRecording.getRecordingMap().entrySet())
        {
            // Add new tab for each sub-recording
            GridPane content = new GridPane();
            content.setAlignment(Pos.CENTER);
            tabPane.getTabs().add(new Tab(entry.getKey(), content));
        }

        // Initialize data processors and whatnot
        FactoryMap factory = Main.getInstance().getFactory();
        IDataProcessor dataProcessor = factory.getProcessor(currentRecording).orElseThrow(IllegalStateException::new);
        IEnvironment env = getEnvironment();
        dataProcessor.initEnvironment(env);
        List<Map.Entry<Double, List<KeyValue>>> keyValues = new ArrayList<>(dataProcessor.generateKeyValues(interpolator).entrySet());

        keyValues.sort(Comparator.comparing(Map.Entry::getKey));

        Iterator<Map.Entry<Double, List<KeyValue>>> keyValItr = keyValues.iterator();

        // Add first keyframe
        List<KeyValue> keyValList = keyValItr.next().getValue();
        keyValList.add(new KeyValue(timeElapsed.textProperty(), "0 seconds"));
        KeyValue[] keyValArray = new KeyValue[keyValList.size()];
        keyValList.toArray(keyValArray);

        keyFrames.add(new KeyFrame(Duration.ZERO,
                                   keyValArray
        ));

        System.out.println("keyValues = " + keyValues);
        while(keyValItr.hasNext())
        {
            Map.Entry<Double, List<KeyValue>> next = keyValItr.next();

            keyValList = next.getValue();
            keyValList.add(new KeyValue(timeElapsed.textProperty(), String.format("%.02f seconds", next.getKey() / 1000)));
            keyValArray = new KeyValue[keyValList.size()];
            keyValList.toArray(keyValArray);

            keyFrames.add(new KeyFrame(Duration.millis(next.getKey()),
                                       keyValArray
            ));
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
        actOnTimeline(timeline, rateSlider.valueProperty().doubleValue());
    }

    private IEnvironment getEnvironment()
    {
        return new IEnvironment()
        {
            @Override
            public AnchorPane getFieldAnchorPane()
            {
                return backdrop;
            }

            @Override
            public GridPane getDataGridPane(String name)
            {
                for(Tab tab : tabPane.getTabs())
                {
                    if(tab.getText().equals(name) && tab.getContent() instanceof GridPane)
                    {
                        return (GridPane) tab.getContent();
                    }
                }
                throw new NullPointerException("Cannot find tab with name: " + name);
            }

            @Override
            public double getScaleFactorX()
            {
                return spatialScaleFactor;
            }

            @Override
            public double getScaleFactorY()
            {
                return spatialScaleFactor;
            }

            @Override
            public ImmutableVector getOrigin()
            {
                return new ImmutableVector(originX, originY);
            }
        };
    }

//    @FXML
//    private void animateSquareKeyframe(Event event)
//    {
//        if(fileChooser.getValue() == null || posChooser.getValue() == null)
//        {
//            System.out.println("Please select a file and position!");
//            return;
//        }
//        clear();
//        // Animation works by interpolating key values between key frames
//        // We store all our keyframes in this handy dandy list
//        List<KeyFrame> keyFrames = new ArrayList<>();
//
//        // Scale our robot appropriately
//        robot.setWidth(initRobotWidth * spatialScaleFactor);
//        robot.setHeight(initRobotHeight * spatialScaleFactor);
//
//
//        originY = backdrop.getHeight() - robot.getHeight();
//
//
//        // Add our first keyframe
//        keyFrames.add(new KeyFrame(Duration.ZERO, new KeyValue(robot.xProperty(), originX),
//                                   new KeyValue(robot.yProperty(), originY)));
//
//        // Center the path on the robot
//        double pathOffsetX = robot.getWidth() / 2;
//        double pathOffsetY = robot.getHeight() / 2;
//
//        double[] waypointInit = waypoints[0];
//
//        double initX = waypointInit[0] * spatialScaleFactor + originX;
//        double initY = -waypointInit[1] * spatialScaleFactor + originY;
//
//        MoveTo initialOffset = new MoveTo(originX + pathOffsetX, originY + pathOffsetY);
//
//        robotPath.getElements().add(initialOffset);
//
//        waypointPath.getElements().add(new MoveTo(initX + pathOffsetX, initY + pathOffsetX));
//
//        // Draw the path -- where our robot was told to go
//        for(int i = 0; i < waypoints.length; i++)
//        {
//            if(i == 0)
//            {
//                continue;
//            }
//            double[] waypoint = waypoints[i];
//            double x = waypoint[0] * spatialScaleFactor + originX;
//
//            // We need this negative since positive y is downwards in JavaFX
//            double y = -waypoint[1] * spatialScaleFactor + originY;
//
//            LineTo lineTo = new LineTo(x + pathOffsetX, y + pathOffsetY);
//            waypointPath.getElements().add(lineTo);
//        }
//
//        // Draw our drive path -- where our robot actually went
//        for(int i = 1; i < robotTraj.length; i++)
//        {
//            // Get this waypoint and the next waypoint
//            double[] waypoint = robotTraj[i - 1];
//
//            double gpX = getX(waypoint[5]);
//            double gpY = getY(waypoint[6]);
//
//            double circleOnRadius = Math.abs(waypoint[7] * spatialScaleFactor);
//            double circleOnX = getX(waypoint[8]);
//            double circleOnY = getY(waypoint[9]);
//
//            double closestPointX = getX(waypoint[11]);
//            double closestPointY = getY(waypoint[12]);
//
//            int pathOnI = (int) waypoint[10];
//
//            double lineSegmentXI = getX(waypoints[pathOnI][0]);
//            double lineSegmentYI = getY(waypoints[pathOnI][1]);
//            double lineSegmentXF = getX(waypoints[pathOnI + 1][0]);
//            double lineSegmentYF = getY(waypoints[pathOnI + 1][1]);
//
//
//            double targetAngle = 90 - waypoint[4] * 180 / Math.PI;
//
//            // Figure out where our robot belongs
//
//            double x = waypoint[1] * spatialScaleFactor + originX;
//
//            // We need this negative since positive y is downwards in JavaFX
//            double y = -waypoint[2] * spatialScaleFactor + originY;
//
//            // Put all our keyvalues (robot pos, robot angle, lookahead) in a keyframe
//            boolean useLine = circleOnRadius > 10000;
//            double lineStartX;
//            double lineStartY;
//            double lineEndX;
//            double lineEndY;
//
//            double robotCenterX = x + robot.getWidth() / 2;
//            double robotCenterY = y + robot.getHeight() / 2;
//            double lineDX = robotCenterX - gpX;
//            double lineDY = robotCenterY - gpY;
//            if(lineDX != 0)
//            {
//                double slope = lineDY / lineDX;
//                MathUtils.Function line = (inp) -> slope * (inp - robotCenterX) + robotCenterY; // pointslope
//                lineStartX = 0;
//                lineStartY = line.get(lineStartX);
//                lineEndX = backdrop.getWidth();
//                lineEndY = line.get(lineEndX);
//            }
//            else
//            {
//                lineStartX = robotCenterX;
//                lineStartY = 0;
//                lineEndX = robotCenterX;
//                lineEndY = backdrop.getHeight();
//            }
//            Interpolator interpolator;
//            if(configManager.getDouble("rate") < 3D / 5D) // if we'll be playing at less than 30 fps
//            {
//                interpolator = Interpolator.EASE_BOTH;
//            }
//            else
//            {
//                interpolator = Interpolator.DISCRETE;
//            }
//
//            keyFrames.add(new KeyFrame(Duration.seconds(waypoint[0]),
//                                       // Robot position
//                                       new KeyValue(robot.xProperty(), x, interpolator),
//                                       new KeyValue(robot.yProperty(), y, interpolator),
//                                       new KeyValue(robot.rotateProperty(), targetAngle, interpolator),
//
//                                       // Goalpoint position
//                                       new KeyValue(goalPoint.centerXProperty(), gpX, interpolator),
//                                       new KeyValue(goalPoint.centerYProperty(), gpY, interpolator),
//
//                                       // Lookahead text
//                                       new KeyValue(lookahead.textProperty(), String.format("%.02f feet", waypoint[3])),
//
//                                       // Curvature pos
//                                       new KeyValue(constantCurvature.centerXProperty(), circleOnX, interpolator),
//                                       new KeyValue(constantCurvature.centerYProperty(), circleOnY, interpolator),
//                                       new KeyValue(constantCurvature.radiusProperty(), circleOnRadius, interpolator),
//
//                                       // Whether to use the circle or the line
//                                       new KeyValue(constantCurvature.visibleProperty(), !useLine, interpolator),
//                                       new KeyValue(constantCurvatureLine.visibleProperty(), useLine, interpolator),
//
//                                       // Line position
//                                       new KeyValue(constantCurvatureLine.startXProperty(), lineStartX, interpolator),
//                                       new KeyValue(constantCurvatureLine.startYProperty(), lineStartY, interpolator),
//                                       new KeyValue(constantCurvatureLine.endXProperty(), lineEndX, interpolator),
//                                       new KeyValue(constantCurvatureLine.endYProperty(), lineEndY, interpolator),
//
//                                       new KeyValue(currentPathLine.startXProperty(), lineSegmentXI, interpolator),
//                                       new KeyValue(currentPathLine.startYProperty(), lineSegmentYI, interpolator),
//                                       new KeyValue(currentPathLine.endXProperty(), lineSegmentXF, interpolator),
//                                       new KeyValue(currentPathLine.endYProperty(), lineSegmentYF, interpolator),
//
//                                       new KeyValue(closestPoint.centerXProperty(), closestPointX, interpolator),
//                                       new KeyValue(closestPoint.centerYProperty(), closestPointY, interpolator),
//                                       new KeyValue(timeElapsed.textProperty(), String.format("%.02f seconds", waypoint[0])),
//                                       new KeyValue(dCP.textProperty(), String.format("%.02f feet", waypoint[13]))
//            ));
//
//            // Add our position information to the translucent grey path that shows where our robot went
//            robotPath.getElements().add(new LineTo(x + pathOffsetX, y + pathOffsetY));
//        }
//
//        // Create the animation
//
//        if(listener != null)
//        {
//            rateSlider.valueProperty().removeListener(listener);
//        }
//
//        if(timeline != null)
//        {
//            timeline.pause();
//        }
//
//        timeline = new Timeline();
//
//        listener = (observable, oldValue, newValue) -> {
//            double value = newValue.doubleValue();
//            actOnTimeline(timeline, value);
//        };
//
//        rateSlider.valueProperty().addListener(listener);
//
//        // Loop it forever
//        timeline.setCycleCount(Timeline.INDEFINITE);
//
//        // When the animation ends, the robot teleports from the end to the beginning instead of driving backwards
//        timeline.setAutoReverse(false);
//
//        // Add our keyframes to the animation
//        keyFrames.forEach((KeyFrame kf) -> timeline.getKeyFrames().add(kf));
//
//        // Play it
//        timeline.playFromStart();
//        actOnTimeline(timeline, rateSlider.valueProperty().doubleValue());
//    }

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

    private void loadRecording(File jsonFile) throws IOException
    {
        List<String> lines = Files.readAllLines(jsonFile.toPath());
        StringBuilder fileContentsSb = new StringBuilder();
        lines.forEach(fileContentsSb::append);
        String json = fileContentsSb.toString();

        Recording recording = JsonUtils.toObject(Recording.class, json);

        this.currentRecording = recording;


    }
}
