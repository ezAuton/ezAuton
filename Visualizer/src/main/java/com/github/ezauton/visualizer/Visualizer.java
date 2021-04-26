package com.github.ezauton.visualizer;

import com.github.ezauton.recorder.Recording;
import com.github.ezauton.recorder.base.PurePursuitRecorder;
import com.github.ezauton.recorder.base.RobotStateRecorder;
import com.github.ezauton.recorder.base.TankDriveableRecorder;
import com.github.ezauton.visualizer.processor.PurePursuitDataProcessor;
import com.github.ezauton.visualizer.processor.RecordingDataProcessor;
import com.github.ezauton.visualizer.processor.RobotStateDataProcessor;
import com.github.ezauton.visualizer.processor.TankDriveableDataProcessor;
import com.github.ezauton.visualizer.processor.factory.FactoryMap;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;


public class Visualizer extends Application {
    private static Visualizer instance;
    private FactoryMap factory = new FactoryMap();
    private Scene mainScene;

    public static Visualizer getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public FactoryMap getFactory() {
        return factory;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        factory.register(PurePursuitRecorder.class, PurePursuitDataProcessor::new);
        factory.register(RobotStateRecorder.class, RobotStateDataProcessor::new);
        factory.register(TankDriveableRecorder.class, TankDriveableDataProcessor::new);
        factory.register(Recording.class, t -> new RecordingDataProcessor(t, factory));


        // Keep a reference to the window

        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));


        primaryStage.setTitle("PP Player");

        Parent mainRoot = FXMLLoader.load(getClass().getResource("main.fxml"));

        // Display the window

        mainScene = new Scene(mainRoot);
        primaryStage.setScene(mainScene);

        primaryStage.show();
    }

    public Window getStage() {
        return mainScene.getWindow();
    }
}
