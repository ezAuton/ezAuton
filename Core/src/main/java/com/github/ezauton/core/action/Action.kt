package com.github.ezauton.core.action

/**
 * Describes an Action, which is similar to a WPILib Commands, but has both linear, periodic, and other implementations.
 * Additionally, it is not bound to the 20ms periodic timer for WPILib Commands. 👋 Commands! 🚀 🤖
 */
interface Action { // In the purest form an action is of type: suspend () -> Unit
    /**
     * Run the action given a clock 🏃‍️
     *
     * @param actionRunInfo The clock to run the action
     */
    @Throws(Exception::class)
    suspend fun run()

}
