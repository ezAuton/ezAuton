package com.github.ezauton.core.action.require

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import java.util.*

data class LockInfo(val mutex: Mutex, val priority: Int)

class BaseResource(vararg subResources: Resource) : Resource {


  private val subResourcesList = subResources.asList().toMutableList()

  val currentLock = Mutex(false)

  private val lowestPriority get() = queue.firstOrNull()?.priority
  private val queue = PriorityQueue<LockInfo> { o1, o2 -> o1.priority - o2.priority }


  fun dependOn(other: Resource): BaseResource {
    subResourcesList.add(other)
    return this
  }

  override val status: ResourceStatus
    get() {

      val used = subResourcesList.asSequence()
        .map { it.status }
        .filterIsInstance<ResourceStatus.Used>()
        .toList()

      val lowestSubPriority = used
        .map { it.lowestPriority }
        .plus(lowestPriority)
        .filterNotNull()
        .minOrNull()

      if (lowestSubPriority != null) return ResourceStatus.Used(lowestSubPriority)
      if (currentLock.isLocked || used.isNotEmpty()) return ResourceStatus.Used(null)
      return ResourceStatus.Open
    }

  fun pollQueue() {
    val res: LockInfo = queue.poll() ?: return
    res.mutex.unlock()
  }

  override suspend fun takeUnsafe(priority: Int): ResourceHold = coroutineScope {

    if (queue.isEmpty()) {
      val hold = tryTakeUnsafe()
      if (hold != null) return@coroutineScope hold
    }

    val mutex = Mutex(true)
    queue.add(LockInfo(mutex, priority))

    val subHoldsDeferred = subResourcesList.map { resource ->
      async {
        resource.takeUnsafe()
      }
    }

    mutex.lock() // will be opened by queue
    currentLock.lock() // can now grab the current lock
    val subHolds = subHoldsDeferred.awaitAll()
    val combinedHold = subHolds.combine()


    return@coroutineScope object : ResourceHold {
      override fun giveBack() {
        combinedHold.giveBack()
        pollQueue()
        currentLock.unlock()
      }

    }

  }

  override fun tryTakeUnsafe(): ResourceHold? {
    val lockedBase = currentLock.tryLock()
    if (!lockedBase) return null

    val subholds = ArrayList<ResourceHold>(subResourcesList.size)

    for (resource in subResourcesList) {
      val hold = resource.tryTakeUnsafe() ?: break
      subholds.add(hold)
    }

    // we were able to grab everything
    if (subholds.size == subResourcesList.size) {
      return object : ResourceHold {
        override fun giveBack() {
          subholds.forEach {
            it.giveBack()
          }
          pollQueue()
          currentLock.unlock()
        }
      }
    }

    // we weren't able to grab everything -> unlock and return null
    subholds.forEach { it.giveBack() }
    pollQueue()
    currentLock.unlock()

    return null
  }


}
