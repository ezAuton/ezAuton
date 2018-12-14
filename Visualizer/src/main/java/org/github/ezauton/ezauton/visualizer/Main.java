package org.github.ezauton.ezauton.visualizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.github.ezauton.ezauton.recorder.Recording;
import org.github.ezauton.ezauton.recorder.base.PurePursuitRecorder;
import org.github.ezauton.ezauton.recorder.base.RobotStateRecorder;
import org.github.ezauton.ezauton.recorder.base.TankDriveableRecorder;
import org.github.ezauton.ezauton.visualizer.processor.PurePursuitDataProcessor;
import org.github.ezauton.ezauton.visualizer.processor.RecordingDataProcessor;
import org.github.ezauton.ezauton.visualizer.processor.RobotStateDataProcessor;
import org.github.ezauton.ezauton.visualizer.processor.TankDriveableDataProcessor;
import org.github.ezauton.ezauton.visualizer.processor.factory.FactoryMap;


public class Main extends Application
{
    private static Main instance;

    public static Main getInstance()
    {
        return instance;
    }

    private FactoryMap factory = new FactoryMap();

    public FactoryMap getFactory()
    {
        return factory;
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
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

        Scene mainScene = new Scene(mainRoot);
        primaryStage.setScene(mainScene);

        primaryStage.show();
    }
}
