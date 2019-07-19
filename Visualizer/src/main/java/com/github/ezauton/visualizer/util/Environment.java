package com.github.ezauton.visualizer.util;

import com.github.ezauton.conversion.ScalarVector;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public interface Environment {
    /**
     * This returns the anchor-pane with the field as a background. This will allow you to draw lines and rectangles.
     *
     * @return The AnchorPane
     */
    AnchorPane getFieldAnchorPane();

    /**
     * This gives the gridpane where you can put down your key-value pairs.
     *
     * @return The gridpane
     */
    GridPane getDataGridPane(String name);

    /**
     * The scale-factor converts from feet to pixels. Use it to appropriately size your rectangles.
     *
     * @return The scale-factor on the x-axis
     */
    double getScaleFactorX();

    /**
     * The scale-factor converts from feet to pixels. Use it to appropriately size your rectangles.
     *
     * @return The scale-factor on the y-axis
     */
    double getScaleFactorY();

    /**
     * The origin represents the starting point of the robot. Normally, robot odometry will say the robot is at (0, 0) at this point,
     * so we must correct for this so that it can be drawn properly.
     *
     * @return The location of the origin.
     */
    ScalarVector getOrigin();
}
