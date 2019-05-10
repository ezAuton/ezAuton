package com.github.ezauton.wpilib.util;

import com.github.ezauton.core.action.Action;
import com.github.ezauton.core.simulation.ActionScheduler;
import com.github.ezauton.core.utils.EzJoystickButton;
import com.github.ezauton.wpilib.command.CommandCreator;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;

import java.util.function.Supplier;

/**
 * An alternative to WPILib Joy
 */
public class WPIJoystickButton extends JoystickButton implements EzJoystickButton {

    /**
     * Create a joystick button for triggering commands.
     *
     * @param joystick     The GenericHID object that has the button (e.g. EzJoystickButton, KinectStick,
     *                     etc)
     * @param buttonNumber The button number (see {@link GenericHID#getRawButton(int) }
     */
    public WPIJoystickButton(GenericHID joystick, int buttonNumber) {
        super(joystick, buttonNumber);
    }

    @Override
    public void whenPressed(ActionScheduler actionScheduler, Supplier<Action> actionSupplier)
    {
        whenPressed(cmd(actionScheduler, actionSupplier.get()));
    }

    @Override
    public void whileHeld(ActionScheduler actionScheduler, Supplier<Action> actionSupplier)
    {
        whileHeld(cmd(actionScheduler, actionSupplier.get()));
    }

    @Override
    public void whenReleased(ActionScheduler actionScheduler, Supplier<Action> actionSupplier)
    {
        whenReleased(cmd(actionScheduler, actionSupplier.get()));
    }

    @Override
    public void toggleWhenPressed(ActionScheduler actionScheduler, Supplier<Action> actionSupplier)
    {
        toggleWhenPressed(cmd(actionScheduler, actionSupplier.get()));
    }

    @Override
    public void cancelWhenPressed(ActionScheduler actionScheduler, Supplier<Action> actionSupplier)
    {
        cancelWhenPressed(cmd(actionScheduler, actionSupplier.get()));
    }

    private CommandCreator cmd(ActionScheduler actionScheduler, Action runnable) {
        return new CommandCreator(runnable, actionScheduler);
    }
}
