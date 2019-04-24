package com.github.ezauton.core

import com.github.ezauton.core.action.ActionGroup
import com.github.ezauton.core.action.ActionRunInfo
import com.github.ezauton.core.action.BaseAction
import com.github.ezauton.core.action.Action

/**
 *  Create a base action whilst having access to a clock ⏱
 */
fun baseAction(block: (ActionRunInfo) -> Unit): BaseAction {
    return object : BaseAction() {
        override fun run(actionRunInfo: ActionRunInfo) {
            block(actionRunInfo)
        }
    }
}

/**
 * Converts an Action into an ActionWrapper which is used for ActionGroups.
 */
fun Action.wrapType(type: ActionGroup.Type): ActionGroup.ActionWrapper {
    return ActionGroup.ActionWrapper(this, type)
}

