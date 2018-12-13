package com.github.ezauton.wpilibexample.actions;

import com.github.ezauton.wpilibexample.DrivetrainSubsystem;
import com.github.ezauton.wpilibexample.RobotData;
import org.github.ezauton.ezauton.action.ActionGroup;

public class ActionSimpleAuton extends ActionGroup
{
    public ActionSimpleAuton(RobotData robotData, DrivetrainSubsystem drivetrainSubsystem)
    {
        addSequential(new ActionPurePursuit(robotData, drivetrainSubsystem));
        addSequential(new ActionGoStraightVoltage(drivetrainSubsystem));
    }
}
