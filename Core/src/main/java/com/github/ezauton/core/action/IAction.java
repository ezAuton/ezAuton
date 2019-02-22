package com.github.ezauton.core.action;

import java.util.List;

/**
 * Describes an IAction, which is similar to a WPILib Commands, but has both linear, periodic, and other implementations.
 * Additionally, it is not bound to the 20ms periodic timer for WPILib Commands. 👋 Commands! 🚀 🤖
 */
public interface IAction {
    /**
     * Run the action given a clock 🏃‍️
     *
     * @param actionRunInfo The clock to run the action
     */
    void run(ActionRunInfo actionRunInfo) throws Exception;

    /**
     * End the action peacefully ✌️
     */
    void end() throws Exception;

    /**
     * Returns self. Will run onFinish when finished 🏁. Should not overwrite previous runnables, but instead append to
     * list of runnables to run when finished.
     *
     * @param onFinish
     * @return
     */
    IAction onFinish(Runnable onFinish);

    List<Runnable> getFinished();

}

