package com.github.ezauton.core.action

import java.util.ArrayList

/**
 * The base implementation of an Action. Most actions are based off of this class, as it provides an easy, base
 * implementation.
 */
open class BaseAction : Action {

    private val toRun = ArrayList<Runnable>()
    private var runnable: Runnable? = null

    override val finished: List<Runnable>
        get() = toRun

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


    override suspend fun run(actionRunInfo: ActionRunInfo) {
        runnable?.run()
    }

    override fun onFinish(onFinish: Runnable): Action {
        toRun.add(onFinish)
        return this
    }
}
