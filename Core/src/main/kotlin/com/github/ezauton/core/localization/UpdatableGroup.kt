package com.github.ezauton.core.localization

import java.util.ArrayList
import java.util.Arrays

/**
 * Describes a group of multiple things that should be updated in unison
 */
//TODO: Suggestion -- Implement List<Updateable>?
class UpdatableGroup
/**
 * Create a new updateable group
 *
 * @param updateables All the things that should be updated together
 */
(vararg updateables: Updateable) : Updateable {

    private val updateables: MutableList<Updateable>

    init {
        this.updateables = ArrayList(Arrays.asList(*updateables))
    }

    fun add(updateable: Updateable) {
        updateables.add(updateable)
    }

    fun remove(updateable: Updateable) {
        updateables.remove(updateable)
    }

    /**
     * @return If at least one of the Updateables updated
     */
    //TODO: Perhaps should return a parallel list of which updateables succeeded and which failed, alternatively return true if all succeeded
    override fun update(): Boolean {
        var updated = false
        for (updateable in updateables) {
            if (updateable.update()) {
                updated = true
            }
        }
        return updated
    }
}
