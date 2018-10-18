package org.github.ezauton.ezauton.localization;

/**
 * Any class which can be regularly updated
 */
public interface Updateable
{
    /**
     * @return If could update successfully
     */
    boolean update();
}
