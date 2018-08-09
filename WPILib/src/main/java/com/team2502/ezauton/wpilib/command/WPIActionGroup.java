package com.team2502.ezauton.wpilib.command;

import com.team2502.ezauton.command.ActionGroup;
import com.team2502.ezauton.command.IAction;
import com.team2502.ezauton.command.InstantAction;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

import java.util.ArrayList;
import java.util.List;

public class WPIActionGroup extends ActionGroup implements IWPIAction
{

    @Override
    public Command buildWPI()
    {
        CommandGroup commandGroup = new CommandGroup();
        List<Command> withCommands = new ArrayList<>();

        for(ActionWrapper scheduledAction : getScheduledActions())
        {
            IAction action = scheduledAction.getAction();
            if(!(action instanceof IWPIAction))
            {
                throw new IllegalArgumentException("All actions must implement IWPILibAction");
            }
            Command command = ((IWPIAction) action).buildWPI();
            Type type = scheduledAction.getType();

            switch(type)
            {
                case WITH:
                    withCommands.add(command);
                case PARALLEL:
                    commandGroup.addParallel(command);
                    break;
                case SEQUENTIAL:
                    commandGroup.addSequential(command);
                    if(!withCommands.isEmpty())
                    {
                        InstantAction instantAction = new InstantAction(() -> withCommands.forEach(Command::cancel));
                        commandGroup.addSequential(instantAction.buildWPI());
                        withCommands.clear();
                    }
                    break;
            }
        }
        InstantAction instantAction = new InstantAction(() -> onFinish.forEach(Runnable::run));
        commandGroup.addSequential(instantAction.buildWPI());
        return commandGroup;
    }
}
