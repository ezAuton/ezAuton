package com.team2502.ezauton.command;

public interface IAction
{

    /**
     * @return A Thread which acts like a command but can be run faster
     */
    Thread buildThread(long millisPeriod);

    void simulate(long millisPeriod);

    /**
     * Remove from the simulator: note onFinish() Runnables will not be called
     */
    void removeSimulator();

    IAction onFinish(Runnable onFinish);

}
