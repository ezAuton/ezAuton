package com.team2502.ezauton.wpilib.command;

import edu.wpi.first.wpilibj.command.Command;

public interface IWPIAction
{
    /**
     * @return A WPILib command
     */
    Command buildWPI();
}
