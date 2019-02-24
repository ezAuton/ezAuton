package com.github.ezauton.wpilib.command;

import com.github.ezauton.core.action.Action;
import com.github.ezauton.core.simulation.ActionScheduler;
import edu.wpi.first.wpilibj.command.Command;

import java.util.concurrent.Future;

/**
 * Creates a action from an {@link Action}. This allows for abstraction and use of commands in simulators.
 */
public class CommandCreator extends Command {

    private final Action action;
    private final ActionScheduler actionScheduler;
    private Future<Void> voidFuture = null;

    /**
     * Create a action from a PeriodicAction
     *
     * @param action The action to run as a action
     */
    public CommandCreator(Action action, ActionScheduler actionScheduler) {
        this.action = action;
        this.actionScheduler = actionScheduler;
    }

    @Override
    protected void initialize() {
        voidFuture = actionScheduler.scheduleAction(action);
    }

    @Override
    protected void execute() {
        // nothing to execute... everything is in the thread.
    }

    @Override
    protected boolean isFinished() {
        if(voidFuture == null) throw new IllegalStateException("somehow command has not been initialized before running");
        return voidFuture.isDone();
    }

    @Override
    protected void interrupted() {
        if(voidFuture == null) throw new IllegalStateException("somehow command has not been initialized before running");
        try {
            action.interrupted();
        } catch (Exception e) {
            e.printStackTrace();
        }
        voidFuture.cancel(true);
    }
}
