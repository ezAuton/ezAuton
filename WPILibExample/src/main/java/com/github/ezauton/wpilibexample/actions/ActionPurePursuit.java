package com.github.ezauton.wpilibexample.actions;

import com.github.ezauton.wpilibexample.DrivetrainSubsystem;
import com.github.ezauton.wpilibexample.RobotData;
import org.github.ezauton.ezauton.action.ActionGroup;
import org.github.ezauton.ezauton.action.PPCommand;
import org.github.ezauton.ezauton.pathplanning.Path;
import org.github.ezauton.ezauton.pathplanning.purepursuit.ILookahead;
import org.github.ezauton.ezauton.pathplanning.purepursuit.LookaheadBounds;
import org.github.ezauton.ezauton.pathplanning.purepursuit.PPWaypoint;
import org.github.ezauton.ezauton.pathplanning.purepursuit.PurePursuitMovementStrategy;

import java.util.concurrent.TimeUnit;

public class ActionPurePursuit extends ActionGroup
{

    public ActionPurePursuit(RobotData robotData, DrivetrainSubsystem drivetrainSubsystem)
    {
        Path path = new PPWaypoint.Builder()
                .add(0,0,5,5,-5)
                .add(10,10,5,5,-5)
                .add(10,15,0,5,-5)
                .buildPathGenerator()
                .generate(0.05);

        PurePursuitMovementStrategy purePursuitMovementStrategy = new PurePursuitMovementStrategy(path,0.5);

        ILookahead lookahead = new LookaheadBounds(1, 5, 2, 10, drivetrainSubsystem.getLocEstimator());

        PPCommand ppCommand = new PPCommand(20, TimeUnit.MILLISECONDS,purePursuitMovementStrategy,drivetrainSubsystem.getLocEstimator(),
                lookahead,drivetrainSubsystem.getTransLocDriveable());
        addSequential(ppCommand);
    }
}
