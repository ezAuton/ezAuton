package com.github.ezauton.wpilibexample.actions;

import com.github.ezauton.wpilibexample.DrivetrainSubsystem;
import com.github.ezauton.wpilibexample.RobotData;
import org.github.ezauton.ezauton.action.ActionGroup;

public class ActionEverythingM8 extends ActionGroup
{
    public ActionEverythingM8(RobotData robotData, DrivetrainSubsystem drivetrainSubsystem)
    {
        addSequential(new ActionPurePursuit(robotData, drivetrainSubsystem));
        addSequential(new ActionGoStraightVoltage(drivetrainSubsystem));
    }
}
