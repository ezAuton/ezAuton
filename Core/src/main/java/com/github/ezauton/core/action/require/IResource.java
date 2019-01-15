package com.github.ezauton.core.action.require;

public interface IResource {
    /**
     * block until the resource if available and then take control of it
     */
    void take() throws InterruptedException;

    /**
     *
     * @return If the resource is taken
     */
    boolean isTaken();

    /**
     * give the resource back
     */
    void give();

    void assertPossession() throws IllegalStateException;
}
