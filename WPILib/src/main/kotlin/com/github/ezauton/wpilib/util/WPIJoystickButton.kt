package com.github.ezauton.wpilib.util

import com.github.ezauton.core.action.Action
import com.github.ezauton.core.simulation.Scheduler
import com.github.ezauton.core.utils.EzJoystickButton
import com.github.ezauton.wpilib.command.CommandCreator
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.buttons.JoystickButton
import java.util.function.Supplier

/**
 * An alternative to WPILib Joy
 */
class WPIJoystickButton
/**
 * Create a joystick button for triggering commands.
 *
 * @param joystick     The GenericHID object that has the button (e.g. EzJoystickButton, KinectStick,
 * etc)
 * @param buttonNumber The button number (see [GenericHID.getRawButton]
 */
  (joystick: GenericHID?, buttonNumber: Int) : JoystickButton(joystick, buttonNumber), EzJoystickButton {
  override fun whenPressed(action: Action) {
    whenPressed(cmd(scheduler, actionSupplier.get()))
  }

  fun whileHeld(scheduler: Scheduler, actionSupplier: Supplier<Action>) {
    whileHeld(cmd(scheduler, actionSupplier.get()))
  }

  fun whenReleased(scheduler: Scheduler, actionSupplier: Supplier<Action>) {
    whenReleased(cmd(scheduler, actionSupplier.get()))
  }

  fun toggleWhenPressed(scheduler: Scheduler, actionSupplier: Supplier<Action>) {
    toggleWhenPressed(cmd(scheduler, actionSupplier.get()))
  }

  fun cancelWhenPressed(scheduler: Scheduler, actionSupplier: Supplier<Action>) {
    cancelWhenPressed(cmd(scheduler, actionSupplier.get()))
  }

  private fun cmd(scheduler: Scheduler, runnable: Action): CommandCreator {
    return CommandCreator(runnable, scheduler)
  }
}
