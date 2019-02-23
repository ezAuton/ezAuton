package com.github.ezauton.core.kotlin

import com.github.ezauton.core.action.ActionGroup
import com.github.ezauton.core.action.ActionRunInfo
import com.github.ezauton.core.action.BaseAction
import com.github.ezauton.core.action.IAction

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
 * Converts an IAction into an ActionWrapper which is used for ActionGroups.
 */
fun IAction.wrapType(type: ActionGroup.Type): ActionGroup.ActionWrapper {
    return ActionGroup.ActionWrapper(this, type)
}

