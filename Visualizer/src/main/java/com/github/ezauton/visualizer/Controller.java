package com.github.ezauton.visualizer;

import javafx.animation.*;
import com.github.ezauton.core.trajectory.geometry.ImmutableVector;
import com.github.ezauton.core.utils.MathUtils;
import com.github.ezauton.recorder.ISubRecording;
import com.github.ezauton.recorder.JsonUtils;
import com.github.ezauton.recorder.Recording;
import com.github.ezauton.visualizer.processor.factory.FactoryMap;
import com.github.ezauton.visualizer.util.IDataProcessor;
import com.github.ezauton.visualizer.util.IEnvironment;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

enum StartPos {
    LEFT_HAB2(163 / 443D, 485 / 492D),
    LEFT_HAB1(163 / 443D, 421 / 492D),
    CENTER(.5, 421 / 492D),
    RIGHT_HAB2(1 - 163D / 443D, 485 / 492D),
    RIGHT_HAB1(1 - 163D / 443D, 421 / 492D);

    private final double proportionX;
    private final double proportionY;

    StartPos(double proportionX, double proportionY) {

        this.proportionX = proportionX;
        this.proportionY = proportionY;
    }

    public double getProportionX() {
        return proportionX;
    }

    public double getProportionY() {
        return 1 - proportionY;
    }

    @Override
    public String toString() {
        return this.name();
    }
}

public class Controller implements Initializable {

    @FXML
    public Button btnSelectJsonLogFile;

    @FXML
    private TabPane tabPane;

    @FXML
    private Button btnSkipToStart;

    @FXML
    private Button btnSkipToEnd;

    @FXML
    private Button btnAdvanceOneFrame;

    @FXML
    private Button btnRewindOneFrame;

    @FXML
    private Button btnPlayPause;

    @FXML
    AnchorPane backdrop;

    @FXML
    private Slider rateSlider;

    @FXML
    private Label timeElapsed;

    @FXML
    private ChoiceBox<StartPos> posChooser;

    @FXML
    private Label clickedCoordsDisplay;


    /**
     * The coordinate (0, 0) is in the top left corner of the screen. Since driving forwards = up, this is bad.
     * The coordinate pair (originX, originY) dictates the absolute starting position of everything.
     * <p>
     * This way, we can make originY non zero allowing us to actually see the path
     */
    private double originX = -1234;
    private double originY = -1234;

    /**
     * By default, the robot is 2-3 pixels tall. This is much too small to learn anything.
     * This scales everything up so that everything is still proportional to each other but you can at least see it
     */
    private double spatialScaleFactor;

    private Timeline timeline;

    private ChangeListener<Number> rateSliderListener;

    private Recording currentRecording;

    /**
     *
     */
    public Controller() {
        // Read the config file in the resources folder and initialize values appropriately
        String homeDir = System.getProperty("user.home");
        java.nio.file.Path filePath = Paths.get(homeDir, ".ezauton", "config", "visualizer.config");

        try {
            Files.createDirectories(filePath.getParent());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getExtension(String fileName) {
        String ret = fileName.substring(fileName.lastIndexOf(".") + 1);
//        System.out.println("ret = " + ret);
        return ret;
    }

    private static List<File> getAllFilesInDirectory(String dir) {
        List<File> files = new ArrayList<>();
        File folder = new File(dir);
        File[] listOfFiles = folder.listFiles();

        try {
            for (int i = 0; i < Objects.requireNonNull(listOfFiles).length; i++) {
                if (listOfFiles[i].isFile() && getExtension(listOfFiles[i].getName()).equalsIgnoreCase("json")) {
                    files.add(listOfFiles[i]);
                }
            }
            return files;
        } catch (NullPointerException e) {
            folder.mkdir();
            return new ArrayList<>();
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        backdrop.heightProperty().addListener((heightProp, oldHeight, newHeight) -> spatialScaleFactor = newHeight.doubleValue() / 30.0156D
        );

        backdrop.widthProperty().addListener((widthProp, oldWidth, newWidth) ->
                {
                    try {
                        originX = posChooser.getValue().getProportionX() * newWidth.doubleValue();
                    } catch (NullPointerException e) {
                        // this is ok
                    }
                }
        );


        // Make sure the circle always stays with the robot

        backdrop.setOnMouseClicked(this::displayRealWorldCoordsOnClick);

        List<File> listOfCSVs = getAllFilesInDirectory(Paths.get(System.getProperty("user.home"), ".ezauton").toString());


        posChooser.getItems().addAll(StartPos.values());
        posChooser.valueProperty().addListener((selectedProp, oldSelected, newSelected) -> {
            originX = posChooser.getValue().getProportionX() * backdrop.getWidth();
            originY = backdrop.getHeight() - posChooser.getValue().getProportionY() * backdrop.getHeight();
            animateSquareKeyframe(null);
        });


//        initRobotWidth = robot.getWidth();
//        initRobotHeight = robot.getHeight();
    }


    /**
     * Clear the tab pane to the right and the canvas with the field on it
     */
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
    private void animateSquareKeyframe(Event event) {
        // must have a file and position
        if (this.currentRecording == null || posChooser.getValue() == null) {
            System.err.println("Please select a file and a position");
            return;
        }

        // Animation works by interpolating key values between key frames
        // We store all our keyframes in this handy dandy list
        List<KeyFrame> keyFrames = new ArrayList<>();

        Interpolator interpolator = Interpolator.DISCRETE;

        // Clear everything
        clear();

        for (Map.Entry<String, ISubRecording> entry : currentRecording.getRecordingMap().entrySet()) {
            // Add new tab for each sub-recording
            GridPane content = new GridPane();
            content.setAlignment(Pos.CENTER);
            tabPane.getTabs().add(new Tab(entry.getKey(), content));
        }

        // Initialize data processors and whatnot
        FactoryMap factory = Visualizer.getInstance().getFactory();

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
        while (keyValItr.hasNext()) {
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

        if (rateSliderListener != null) {
            rateSlider.valueProperty().removeListener(rateSliderListener);
        }

        if (timeline != null) {
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


        btnPlayPause.setOnMouseClicked((e) -> {
            System.out.println("timeline.getStatus() = " + timeline.getStatus());
            if(timeline.getStatus() == Animation.Status.RUNNING) {
                pause();
            }
            else {
                play();
            }
        });

        btnAdvanceOneFrame.setOnMouseClicked((e) -> {
            pause();
            timeline.jumpTo(timeline.getCurrentTime().add(new Duration(1000 / timeline.getTargetFramerate())));
        });

        btnRewindOneFrame.setOnMouseClicked((e) -> {
            pause();
            timeline.jumpTo(timeline.getCurrentTime().subtract(new Duration(1000 / timeline.getTargetFramerate())));
        });

        btnSkipToStart.setOnMouseClicked((e) -> {
            pause();
            timeline.jumpTo(new Duration(3000 / timeline.getTargetFramerate())); // skip 3 frames so that stuff will be in the right spot.

        });

        btnSkipToEnd.setOnMouseClicked((e) -> {
            pause();
            timeline.jumpTo(timeline.getCycleDuration());
        });

        // Play it
        timeline.playFromStart();
        btnPlayPause.setText("Pause");
        actOnTimeline(timeline, rateSlider.valueProperty().doubleValue());
    }

    private void pause() {
        timeline.pause();
        btnPlayPause.setText("Play");
    }
    private void play() {
        if(timeline.getStatus() == Animation.Status.STOPPED) {
            timeline.playFromStart();
            btnPlayPause.setText("Pause");
        }
        else {
            timeline.play();
            btnPlayPause.setText("Pause");
        }
    }

    private IEnvironment getEnvironment() {
        return new IEnvironment() {
            @Override
            public AnchorPane getFieldAnchorPane() {
                return backdrop;
            }

            @Override
            public GridPane getDataGridPane(String name) {
                for (Tab tab : tabPane.getTabs()) {
                    if (tab.getText().equals(name) && tab.getContent() instanceof GridPane) {
                        return (GridPane) tab.getContent();
                    }
                }
                throw new NullPointerException("Cannot find tab with name: " + name);
            }

            @Override
            public double getScaleFactorX() {
                return spatialScaleFactor;
            }

            @Override
            public double getScaleFactorY() {
                return spatialScaleFactor;
            }

            @Override
            public ImmutableVector getOrigin() {
                return new ImmutableVector(originX, originY);
            }
        };
    }

    /**
     * Handles pausing/playing the timeline based on the rate slider
     */
    private void actOnTimeline(Timeline timeline, double value) {
        if (MathUtils.epsilonEquals(0, value)) {
            timeline.pause();
        } else {
            timeline.play();
            timeline.setRate(value);
        }
    }

    @FXML
    private void displayRealWorldCoordsOnClick(MouseEvent e) {
        double xFt = (e.getX() - originX) / spatialScaleFactor;
        double yFt = ( originY - e.getY()) / spatialScaleFactor;

        if(MathUtils.epsilonEquals(originX, -1234) && MathUtils.epsilonEquals(originY, -1234)) {
            clickedCoordsDisplay.setText("Select a starting position first.");
        }
        else
        {
            clickedCoordsDisplay.setText(String.format("(%f, %f)", xFt, yFt));
        }
    }

    @FXML
    private void selectFile(Event e) {
        e.consume();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select JSON Recording");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("JSON file", "*.json"));
        try
        {
            File jsonFile = fileChooser.showOpenDialog(Visualizer.getInstance().getStage());
            loadRecording(jsonFile);
            btnSelectJsonLogFile.setText(jsonFile.getName());
        }
        catch(Exception err)
        {
            err.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error deserializing log file");
            alert.setContentText("Are you sure that you picked a json recording? \n See stacktrace in console.");
            alert.showAndWait();
        }
    }

    /**
     * Loads a recording from a .json file
     *
     * @param jsonFile
     * @throws IOException If the file cannot be read from
     */
    private void loadRecording(File jsonFile) throws IOException {
        List<String> lines = Files.readAllLines(jsonFile.toPath());
        StringBuilder fileContentsSb = new StringBuilder();
        lines.forEach(fileContentsSb::append);
        String json = fileContentsSb.toString();

        this.currentRecording = JsonUtils.toObject(Recording.class, json);
        animateSquareKeyframe(null);
    }
}
