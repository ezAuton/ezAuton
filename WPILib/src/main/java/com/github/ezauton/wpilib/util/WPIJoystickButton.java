package com.github.ezauton.wpilib.util;

import com.github.ezauton.core.action.Action;
import com.github.ezauton.core.simulation.Scheduler;
import com.github.ezauton.core.utils.EzJoystickButton;
import com.github.ezauton.wpilib.command.CommandCreator;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

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
    public void whenPressed(Scheduler scheduler, Supplier<Action> actionSupplier) {
        whenPressed(cmd(scheduler, actionSupplier.get()));
    }

    @Override
    public void whileHeld(Scheduler scheduler, Supplier<Action> actionSupplier) {
        whileHeld(cmd(scheduler, actionSupplier.get()));
    }

    @Override
    public void whenReleased(Scheduler scheduler, Supplier<Action> actionSupplier) {
        whenReleased(cmd(scheduler, actionSupplier.get()));
    }

    @Override
    public void toggleWhenPressed(Scheduler scheduler, Supplier<Action> actionSupplier) {
        toggleWhenPressed(cmd(scheduler, actionSupplier.get()));
    }

    @Override
    public void cancelWhenPressed(Scheduler scheduler, Supplier<Action> actionSupplier) {
        cancelWhenPressed(cmd(scheduler, actionSupplier.get()));
    }

    private CommandCreator cmd(Scheduler scheduler, Action runnable) {
        return new CommandCreator(runnable, scheduler);
    }
}
