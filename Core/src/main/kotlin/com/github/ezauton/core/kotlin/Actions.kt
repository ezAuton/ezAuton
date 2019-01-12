package com.github.ezauton.core.kotlin

import com.github.ezauton.core.action.ActionGroup
import com.github.ezauton.core.action.BaseAction
import com.github.ezauton.core.action.IAction
import com.github.ezauton.core.utils.IClock

fun baseAction(block: (IClock) -> Unit): BaseAction {
    return object: BaseAction()
    {
        override fun run(clock: IClock) {
            block(clock)
        }
    }
}

fun IAction.wrap(type: ActionGroup.Type): ActionGroup.ActionWrapper {
    return ActionGroup.ActionWrapper(this,type)
}