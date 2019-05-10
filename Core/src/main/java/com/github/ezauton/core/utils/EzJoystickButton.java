package com.github.ezauton.core.utils;

import com.github.ezauton.core.action.Action;
import com.github.ezauton.core.simulation.ActionScheduler;

import java.util.function.Supplier;

public interface EzJoystickButton {
    boolean get();

    /**
     * Starts the given command whenever the button is newly pressed.
     *
     * @param runnable runnable to run
     */
    void whenPressed(ActionScheduler actionScheduler, final Supplier<Action> actionSupplier);

    /**
     * Constantly starts the given command while the button is held.
     * <p>
     * {@link Command#start()} will be called repeatedly while the button is held, and will be
     * canceled when the button is released.
     *
     * @param runnable the runnable to start
     */
    void whileHeld(ActionScheduler actionScheduler, final Supplier<Action> actionSupplier);

    /**
     * Starts the command when the button is released.
     *
     * @param runnable the runnable to start
     */
    void whenReleased(ActionScheduler actionScheduler, final Supplier<Action> actionSupplier);

    /**
     * Toggles the command whenever the button is pressed (on then off then on).
     *
     * @param runnable the runnable to start
     */
    void toggleWhenPressed(ActionScheduler actionScheduler, final Supplier<Action> actionSupplier);

    /**
     * Cancel the command when the button is pressed.
     *
     * @param runnable the runnable to start
     */
    void cancelWhenPressed(ActionScheduler actionScheduler, final Supplier<Action> actionSupplier);
}
