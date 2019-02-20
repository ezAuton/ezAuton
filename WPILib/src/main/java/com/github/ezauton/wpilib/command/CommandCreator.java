package com.github.ezauton.wpilib.command;

import com.github.ezauton.core.action.IAction;
import com.github.ezauton.core.action.ThreadBuilder;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Creates a action from an {@link IAction}. This allows for abstraction and use of commands in simulators.
 */
public class CommandCreator extends Command {

    private final IAction action;
    private Thread thread;
    private boolean finished = false;

    /**
     * Create a action from a PeriodicAction
     *
     * @param action The action to run as a action
     */
    public CommandCreator(IAction action) {
        this.action = action;
    }

    @Override
    protected void initialize() {
        thread = new ThreadBuilder(action).start();
        action.onFinish(() -> finished = true);
    }

    @Override
    protected void execute() {
        // nothing to execute... everything is in the thread.
    }

    @Override
    protected boolean isFinished() {
        return finished;
    }

    @Override
    protected void end() {
        action.end();
        thread.stop();
    }

    @Override
    protected void interrupted() {
        action.end();
        thread.interrupt();
    }
}
