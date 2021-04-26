package com.github.ezauton.core.action.require

interface Resource {
  val status: ResourceStatus

  /**
   * wait until the resource if available and then take control of it
   */
  suspend fun take(priority: Int = 0): ResourceHold

}

val Resource.isTakenByAnyone get() = status is ResourceStatus.Used

interface ResourceHold {
  fun giveBack()
}

fun ResourceHold.combine(other: ResourceHold): ResourceHold {
  return object : ResourceHold {
    override fun giveBack() {
      this@combine.giveBack()
      other.giveBack()
    }
  }
}

fun Collection<ResourceHold>.combine() = reduce { acc, resourceHold -> acc.combine(resourceHold) }

sealed class ResourceStatus {
  object Open : ResourceStatus()
  data class Used(val priority: Int) : ResourceStatus()
}
