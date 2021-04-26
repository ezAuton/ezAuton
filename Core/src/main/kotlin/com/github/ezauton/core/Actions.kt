package com.github.ezauton.core

import com.github.ezauton.core.action.Action
import com.github.ezauton.core.action.ActionGroup

/**
 * Converts an Action into an ActionWrapper which is used for ActionGroups.
 */
fun Action.wrapType(type: ActionGroup.Type): ActionGroup.ActionWrapper {
  return ActionGroup.ActionWrapper(this, type)
}
