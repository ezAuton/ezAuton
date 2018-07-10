package com.team2502.ezauton.command;

import edu.wpi.first.wpilibj.command.Command;

public interface IAction
{

    /**
     * @return A WPILib command
     */
    Command buildWPI();

    /**
     * @return A Thread which acts like a command but can be run faster
     */
    Thread buildThread(long millisPeriod);

    void simulate(long millisPeriod);

    /**
     * Remove from the simulator: note onFinish() Runnables will not be called
     */
    void removeSimulator();

    void onFinish(Runnable onFinish);

}
