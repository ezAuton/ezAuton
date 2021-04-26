package com.github.ezauton.core.localization

/**
 * Any class which can be regularly updated
 */
interface Updatable {
  /**
   * @return If could update successfully
   */
  fun update(): Boolean
}


fun Iterable<Updatable>.update() = forEach { it.update() }
