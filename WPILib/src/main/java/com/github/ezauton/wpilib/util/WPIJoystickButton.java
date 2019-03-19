package com.github.ezauton.wpilib.util;

import com.github.ezauton.core.utils.EzJoystickButton;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 * An alternative to WPILib Joy
 */
public class WPIJoystickButton implements EzJoystickButton {

    private final edu.wpi.first.wpilibj.buttons.JoystickButton joystickButton;

    /**
     * Create a joystick button for triggering commands.
     *
     * @param joystick     The GenericHID object that has the button (e.g. EzJoystickButton, KinectStick,
     *                     etc)
     * @param buttonNumber The button number (see {@link GenericHID#getRawButton(int) }
     */
    public WPIJoystickButton(GenericHID joystick, int buttonNumber) {
        joystickButton = new edu.wpi.first.wpilibj.buttons.JoystickButton(joystick, buttonNumber);
    }

    public boolean get() {
        return joystickButton.get();
    }

    /**
     * Starts the given command whenever the button is newly pressed.
     *
     * @param runnable runnable to run
     */
    public void whenPressed(final Runnable runnable) {
        joystickButton.whenPressed(cmd(runnable));
    }

    /**
     * Constantly starts the given command while the button is held.
     * <p>
     * {@link Command#start()} will be called repeatedly while the button is held, and will be
     * canceled when the button is released.
     *
     * @param runnable the runnable to start
     */
    public void whileHeld(final Runnable runnable) {
        joystickButton.whileHeld(cmd(runnable));
    }

    /**
     * Starts the command when the button is released.
     *
     * @param runnable the runnable to start
     */
    public void whenReleased(final Runnable runnable) {
        joystickButton.whenReleased(cmd(runnable));
    }

    /**
     * Toggles the command whenever the button is pressed (on then off then on).
     *
     * @param runnable the runnable to start
     */
    public void toggleWhenPressed(final Runnable runnable) {
        joystickButton.toggleWhenPressed(cmd(runnable));
    }

    /**
     * Cancel the command when the button is pressed.
     *
     * @param runnable the runnable to start
     */
    public void cancelWhenPressed(final Runnable runnable) {
        joystickButton.cancelWhenPressed(cmd(runnable));
    }

    private InstantCommand cmd(Runnable runnable) {
        return new InstantCommand(runnable);
    }
}
