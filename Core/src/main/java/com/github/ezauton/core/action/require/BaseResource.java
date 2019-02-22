package com.github.ezauton.core.action.require;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class BaseResource implements IResource {


    private ReentrantLock lock = new ReentrantLock();

    private Set<IResource> subResources = new HashSet<>();

    /**
     * Add another resource sub-dependency.
     *
     * @param resource
     * @return
     */
    public BaseResource dependOn(IResource resource) {
        subResources.add(resource);
        return this;
    }

    @Override
    public final void take() throws InterruptedException {
        lock.lock();
        for (IResource subResource : subResources) {
            subResource.take();
        }
    }

    @Override
    public boolean isTakenByAnyone() {
        if (lock.isLocked()) return true;
        return subResources.stream().anyMatch(IResource::isTakenByAnyone);
    }

    @Override
    public void giveBack() {
        lock.unlock();
        for (IResource subResource : subResources) {
            subResource.giveBack();
        }
    }

    @Override
    public void assertPossession() throws IllegalStateException {
        if (!lock.isHeldByCurrentThread())
            throw new IllegalStateException("You must have possession of this resource " +
                    "(use Resource#take())");
        for (IResource subResource : subResources) {
            subResource.assertPossession();
        }
    }
}
