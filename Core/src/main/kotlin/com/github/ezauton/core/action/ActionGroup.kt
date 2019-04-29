package com.github.ezauton.core.action

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*


/**
 * Describes a group of multiple IActions which itself is also an Action
 *
 *
 * Create a Thread from this Action group. The ActionGroup blocks until all sub actions (including parallels) are
 * finished. The reason for this is mentioned
 * [here](https://vorpus.org/blog/notes-on-structured-concurrency-or-go-statement-considered-harmful/).
 */
class ActionGroup : Action {

    private var scheduledActions: Queue<ActionWrapper>

    /**
     * Creates an Action Group comprised of different kinds of commands (i.e sequential, parallel, with)
     *
     * @param scheduledActions The ActionWrappers to run
     */
    constructor(vararg scheduledActions: ActionWrapper) {
        this.scheduledActions = LinkedList(Arrays.asList(*scheduledActions))
    }

    /**
     * Create an empty ActionGroup
     */
    constructor() {
        this.scheduledActions = LinkedList()
    }

    /**
     * Add a sequential Action to the actions that we will run
     *
     * @param action The Action to run
     * @return this
     */
    fun addSequential(action: Action): ActionGroup {
        this.scheduledActions.add(ActionWrapper(action, Type.SEQUENTIAL))

        return this
    }

    /**
     * Add a sequential Action to the actions that we will run
     *
     * @param runnable The Action to run
     * @return this
     */
    fun addSequential(block: ActionFunc): ActionGroup {
        this.scheduledActions.add(ActionWrapper(block.toAction(), Type.SEQUENTIAL))
        return this
    }

    /**
     * Add a daemonic Action to the actions that we will run. It will run in parallel with another action, except it wil end at the same time as the other action.
     *
     * @param action The Action to run
     * @return this
     */
    fun with(action: Action): ActionGroup {
        this.scheduledActions.add(ActionWrapper(action, Type.WITH))
        return this
    }

    /**
     * Add a daemonic Action to the actions that we will run. It will run in parallel with another action, except it wil end at the same time as the other action.
     *
     * @param runnable The Runnable to run
     * @return this
     */
    fun with(block: ActionFunc): ActionGroup {
        this.scheduledActions.add(ActionWrapper(block.toAction(), Type.WITH))
        return this
    }

    /**
     * Add a parallel Action to the actions that we will run. It will run in parallel and will end in its own time.
     *
     * @param action The action to run
     * @return this
     */
    fun addParallel(action: Action): ActionGroup {
        this.scheduledActions.add(ActionWrapper(action, Type.PARALLEL))
        return this
    }

    /**
     * Add a parallel Action to the actions that we will run. It will run in parallel and will end in its own time.
     *
     * @param runnable The Runnable to run
     * @return this
     */
    fun addParallel(block: ActionFunc): ActionGroup {
        this.scheduledActions.add(ActionWrapper(block.toAction(), Type.PARALLEL))
        return this
    }

    override suspend fun ActionContext.run() = coroutineScope {
        val withActions = ArrayList<Job>()
        val jobs = ArrayList<Job>()

        for (scheduledAction in scheduledActions) {
            val action = scheduledAction.action
            val submit = launch {
                with(action) {
                    run()
                }
            }

            jobs.add(submit)

            when (scheduledAction.type) {
                Type.WITH -> withActions.add(submit)
                Type.PARALLEL -> {
                }
                Type.SEQUENTIAL -> {
                    /*
                    It might seem odd why we are scheduling even sequential actions.
                    You might ask — "why can't we just run the callable directly?"

                    The reason is we would never get the interruption exception...SA WOULD
                    we are not the ones blocking ... THE SA is.
                    Thus, to be able to process InterruptedExceptions, the action must be running detached from
                    the action group.
                     */

                    submit.join() // TODO: should auto cancel since in coroutine scope... right?

                    withActions.forEach { it.cancel() }
                    withActions.clear()
                }
            }
        }
    }

    /**
     * Provides a way to describe the concurrency of an action
     */
    enum class Type {
        /**
         * Runs in parallel to everything else.
         */
        PARALLEL,

        /**
         * Runs sequentially. Only one sequential action can be running at a time
         */
        SEQUENTIAL,

        /**
         * Runs in parallel to everything else, but will end when the current sequential action ends.
         */
        WITH
    }

    /**
     * Describes a wrapper class for Action that also contains the concurrency level
     *
     * @see Type
     */
    data class ActionWrapper(val action: Action, val type: Type)

}
