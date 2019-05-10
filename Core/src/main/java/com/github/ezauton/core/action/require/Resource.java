package com.github.ezauton.core.action.require;

public interface Resource {
    /**
     * block until the resource if available and then take control of it
     */
    void take() throws InterruptedException;

    /**
     * @return If the resource is taken
     */
    boolean isTakenByAnyone();

    /**
     * give the resource back
     */
    void giveBack();

    void assertPossession() throws IllegalStateException;
}
