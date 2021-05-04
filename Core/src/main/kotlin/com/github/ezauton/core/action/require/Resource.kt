package com.github.ezauton.core.action.require

interface Resource {
  val status: ResourceStatus

  /**
   * wait until the resource if available and then take control of it
   *
   */
  @Deprecated("Use the scoped version")
  suspend fun takeUnsafe(priority: Int = 0): ResourceHold
  fun tryTakeUnsafe(): ResourceHold?
}

suspend inline fun <A : Resource, T> A.take(priority: Int = 0, block: (A) -> T): T {
  val hold = takeUnsafe(priority)
  val res = block(this)
  hold.giveBack()
  return res
}

inline fun <A : Resource, T> A.forceTake(block: (A) -> T): T {
  val hold = tryTakeUnsafe() ?: throw IllegalStateException("no hold")
  val res = block(this)
  hold.giveBack()
  return res
}

val Resource.isTaken get() = status is ResourceStatus.Used
val Resource.isOpen get() = status is ResourceStatus.Open

interface ResourceHold {
  fun giveBack()

  companion object Empty : ResourceHold {
    override fun giveBack() {}
  }
}

fun ResourceHold.combine(other: ResourceHold): ResourceHold {
  return object : ResourceHold {
    override fun giveBack() {
      this@combine.giveBack()
      other.giveBack()
    }
  }
}

fun Collection<ResourceHold>.combine(): ResourceHold = when (size) {
  0 -> ResourceHold.Empty
  else -> reduce { acc, resourceHold -> acc.combine(resourceHold) }
}

sealed class ResourceStatus {
  object Open : ResourceStatus()
  data class Used(val priority: Int) : ResourceStatus()
}
