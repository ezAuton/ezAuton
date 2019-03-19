package com.github.ezauton.core.utils;

public interface EzJoystickButton {
    boolean get();

    /**
     * Starts the given command whenever the button is newly pressed.
     *
     * @param runnable runnable to run
     */
    void whenPressed(final Runnable runnable);

    /**
     * Constantly starts the given command while the button is held.
     * <p>
     * {@link Command#start()} will be called repeatedly while the button is held, and will be
     * canceled when the button is released.
     *
     * @param runnable the runnable to start
     */
    void whileHeld(final Runnable runnable);

    /**
     * Starts the command when the button is released.
     *
     * @param runnable the runnable to start
     */
    void whenReleased(final Runnable runnable);

    /**
     * Toggles the command whenever the button is pressed (on then off then on).
     *
     * @param runnable the runnable to start
     */
    void toggleWhenPressed(final Runnable runnable);

    /**
     * Cancel the command when the button is pressed.
     *
     * @param runnable the runnable to start
     */
    void cancelWhenPressed(final Runnable runnable);
}
