package com.team2502.guitools.ppsimulator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application
{
    private static final double IMGWIDTH = 443;
    private static final double IMGHEIGHT = 492;
    private static final double W_TO__H_RATIO = IMGWIDTH / IMGHEIGHT;
    private static final double H_TO_W_RATIO = 1 / W_TO__H_RATIO;
    private Stage window;
    private Scene mainScene;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        // Keep a reference to the window
        window = primaryStage;

        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));


        primaryStage.setTitle("PP Player");

        Parent mainRoot = FXMLLoader.load(getClass().getResource("main.fxml"));

        // Display the window

        mainScene = new Scene(mainRoot);
        primaryStage.setScene(mainScene);
        primaryStage.widthProperty().addListener((widthProp, oldWidth, newWidth) -> {
            double fieldWidth = newWidth.doubleValue() - IMGWIDTH;
            primaryStage.setHeight(fieldWidth * H_TO_W_RATIO);
            System.out.println(fieldWidth * H_TO_W_RATIO);
        });

//        primaryStage.heightProperty().addListener((heightProp, oldHeight, newHeight) -> {
//            primaryStage.setWidth(newHeight.doubleValue() * W_TO__H_RATIO + IMGHEIGHT);
//        });
        primaryStage.show();
    }
}
