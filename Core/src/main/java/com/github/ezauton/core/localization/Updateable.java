package com.github.ezauton.core.localization;

/**
 * Any class which can be regularly updated
 */
public interface Updateable {
    /**
     * @return If could update successfully
     */
    boolean update();
}
