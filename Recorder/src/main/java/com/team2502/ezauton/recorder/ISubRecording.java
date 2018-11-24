package com.team2502.ezauton.recorder;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import javafx.animation.KeyFrame;
import javafx.scene.layout.AnchorPane;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="@class")
public interface ISubRecording
{
    String getName();
    String toJson();
}
