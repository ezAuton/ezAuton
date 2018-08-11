package com.team2502.ezauton.wpilib.command;

import com.team2502.ezauton.command.SimpleAction;
import edu.wpi.first.wpilibj.command.Command;

public abstract class WPILibBaseAction extends SimpleAction implements IWPIAction
{

        /**
     * @return A WPILib command
     */
    @Override
    public Command buildWPI()
    {
        if(used)
        {
            throw new RuntimeException("Action already used!");
        }
        used = true;
        return new CommandCreator(this);
    }
}
