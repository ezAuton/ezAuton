package com.team2502.guitools.ppsimulator;

import javafx.scene.layout.AnchorPane;

public interface IEnvironment
{
    AnchorPane getAnchorPane();
    double getScaleFactorX();
    double getScaleFactorY();
    double getOriginX();
    double getOriginY();
}
