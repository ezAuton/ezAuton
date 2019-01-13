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
import org.github.ezauton.ezauton.visualizer.util.IDataProcessor;
import org.github.ezauton.ezauton.visualizer.util.IEnvironment;

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

    private Timeline timeline;

    private ChangeListener<Number> rateSliderListener;

    private Recording currentRecording;

    /**
     */
    public Controller()
    {
        // Read the config file in the resources folder and initialize values appropriately
        String homeDir = System.getProperty("user.home");
        java.nio.file.Path filePath = Paths.get(homeDir, ".ezauton", "config", "visualizer.config");

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
            if(!Files.exists(filePath))
            {
                Files.createFile(filePath);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private static String getExtension(String fileName)
    {
        String ret = fileName.substring(fileName.lastIndexOf(".") + 1);
//        System.out.println("ret = " + ret);
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


    /**
     * Clear the tab pane to the right and the canvas with the field on it
     */
    private void clear()
    {
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
        // must have a file and position
        if(fileChooser.getValue() == null || posChooser.getValue() == null)
        {
            throw new IllegalArgumentException("Please select a file and position!");
        }

        // Animation works by interpolating key values between key frames
        // We store all our keyframes in this handy dandy list
        List<KeyFrame> keyFrames = new ArrayList<>();

        originY = backdrop.getHeight();

        Interpolator interpolator = Interpolator.DISCRETE;

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

        // currentRecorder... holds values of RECORDINGS... maps to RECORDING PROCESSORS
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

//        System.out.println("keyValues = " + keyValues);
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

        if(rateSliderListener != null)
        {
            rateSlider.valueProperty().removeListener(rateSliderListener);
        }

        if(timeline != null)
        {
            timeline.pause();
        }

        timeline = new Timeline();

        rateSliderListener = (observable, oldValue, newValue) -> {
            double value = newValue.doubleValue();
            actOnTimeline(timeline, value);
        };

        rateSlider.valueProperty().addListener(rateSliderListener);

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

    /**
     * Handles pausing/playing the timeline based on the rate slider
     */
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

    /**
     * Loads a recording from a .json file
     *
     * @param jsonFile
     * @throws IOException If the file cannot be read from
     */
    private void loadRecording(File jsonFile) throws IOException
    {
        List<String> lines = Files.readAllLines(jsonFile.toPath());
        StringBuilder fileContentsSb = new StringBuilder();
        lines.forEach(fileContentsSb::append);
        String json = fileContentsSb.toString();

        this.currentRecording = JsonUtils.toObject(Recording.class, json);
    }
}
