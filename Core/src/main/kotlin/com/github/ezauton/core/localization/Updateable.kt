package com.github.ezauton.core.localization

/**
 * Any class which can be regularly updated
 */
interface Updateable {
    /**
     * @return If could update successfully
     */
    fun update(): Boolean
}
