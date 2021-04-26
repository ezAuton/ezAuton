package com.github.ezauton.core.utils

import com.github.ezauton.core.action.Action
import com.github.ezauton.core.simulation.Scheduler

import java.util.function.Supplier

interface EzJoystickButton {
  fun get(): Boolean

  /**
   * Starts the given command whenever the button is newly pressed.
   *
   * @param runnable runnable to run
   */
  fun whenPressed(scheduler: Scheduler, actionSupplier: Supplier<Action>)

  /**
   * Constantly starts the given command while the button is held.
   *
   *
   * [Command.start] will be called repeatedly while the button is held, and will be
   * canceled when the button is released.
   *
   * @param runnable the runnable to start
   */
  fun whileHeld(scheduler: Scheduler, actionSupplier: Supplier<Action>)

  /**
   * Starts the command when the button is released.
   *
   * @param runnable the runnable to start
   */
  fun whenReleased(scheduler: Scheduler, actionSupplier: Supplier<Action>)

  /**
   * Toggles the command whenever the button is pressed (on then off then on).
   *
   * @param runnable the runnable to start
   */
  fun toggleWhenPressed(scheduler: Scheduler, actionSupplier: Supplier<Action>)

  /**
   * Cancel the command when the button is pressed.
   *
   * @param runnable the runnable to start
   */
  fun cancelWhenPressed(scheduler: Scheduler, actionSupplier: Supplier<Action>)
}
