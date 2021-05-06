package com.github.ezauton.core.localization


fun Iterable<Updatable>.update() = forEach { it.update() }
