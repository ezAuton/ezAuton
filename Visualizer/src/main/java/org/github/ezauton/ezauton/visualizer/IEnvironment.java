package org.github.ezauton.ezauton.visualizer;

import javafx.scene.layout.AnchorPane;

public interface IEnvironment
{
    AnchorPane getAnchorPane();
    double getScaleFactorX();
    double getScaleFactorY();
    double getOriginX();
    double getOriginY();
}
