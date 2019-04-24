package com.github.ezauton.core.action

/**
 * Describes an Action, which is similar to a WPILib Commands, but has both linear, periodic, and other implementations.
 * Additionally, it is not bound to the 20ms periodic timer for WPILib Commands. 👋 Commands! 🚀 🤖
 */
interface Action {

    val finished: List<Runnable>
    /**
     * Run the action given a clock 🏃‍️
     *
     * @param actionRunInfo The clock to run the action
     */
    @Throws(Exception::class)
    suspend fun run(actionRunInfo: ActionRunInfo)

    /**
     * Returns self. Will run onFinish when finished 🏁. Should not overwrite previous runnables, but instead append to
     * list of runnables to run when finished.
     *
     * @param onFinish
     * @return
     */
    fun onFinish(onFinish: Runnable): Action

}
