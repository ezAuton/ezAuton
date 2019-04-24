package com.github.ezauton.core.action

/**
 * The base implementation of an Action. Most actions are based off of this class, as it provides an easy, base
 * implementation.
 */
open class BaseAction : Action {

    private var runnable: Runnable? = null

    /**
     * Create an empty [BaseAction] which does not run anything
     */
    constructor()

    /**
     * Runs the provided [Runnable] once.
     *
     *
     * To avoid confusion on whether [Runnable]s execute sequentially or in parallel, only one runnable is allowed.
     * To easily create an action with multiple [Runnable]s or sub actions, see [ActionGroup]
     *
     * @param
     */
    constructor(runnable: Runnable) {
        this.runnable = runnable
    }


    override suspend fun run() {
        runnable?.run()
    }
}
