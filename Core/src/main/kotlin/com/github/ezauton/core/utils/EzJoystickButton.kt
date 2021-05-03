package com.github.ezauton.core.utils

import com.github.ezauton.core.action.Action

interface EzJoystickButton {
  fun get(): Boolean

  /**
   * Starts the given command whenever the button is newly pressed.
   *
   * @param runnable runnable to run
   */
  fun <T> whenPressed(action: Action<T>)

  /**
   * Constantly starts the given command while the button is held.
   *
   *
   * [Command.start] will be called repeatedly while the button is held, and will be
   * canceled when the button is released.
   *
   * @param runnable the runnable to start
   */
  fun <T> whileHeld(actionSupplier: Action<T>)

  /**
   * Starts the command when the button is released.
   *
   * @param runnable the runnable to start
   */
  fun <T> whenReleased(action: Action<T>)

  /**
   * Toggles the command whenever the button is pressed (on then off then on).
   *
   * @param runnable the runnable to start
   */
  fun <T> toggleWhenPressed(action: Action<T>)

  /**
   * Cancel the command when the button is pressed.
   *
   * @param runnable the runnable to start
   */
  fun <T> cancelWhenPressed(action: Action<T>)
}
