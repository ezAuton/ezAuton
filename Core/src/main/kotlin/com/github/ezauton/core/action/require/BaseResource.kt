package com.github.ezauton.core.action.require

import kotlinx.coroutines.sync.Mutex
import java.util.PriorityQueue

data class LockInfo(val mutex: Mutex?, val priority: Int)

class BaseResource(private vararg val subResources: Resource) : Resource {

    private val lowestPriority get() = queue.first().priority
    private val queue = PriorityQueue<LockInfo> { o1, o2 -> o1.priority - o2.priority }

    override val status: ResourceStatus
        get() {
            if (queue.isEmpty()) return ResourceStatus.Open
            return ResourceStatus.Used(lowestPriority)
        }

    fun pollQueue(lockInfo: LockInfo) {
        val get = queue.first()
        if (lockInfo !== get) throw IllegalArgumentException("Queue is wrong")
        queue.poll()
        queue.firstOrNull()?.mutex?.unlock()
    }

    override suspend fun take(priority: Int): ResourceHold {
        val mutex = if (priority < lowestPriority) null else Mutex(true)
        val lockInfo = LockInfo(mutex, priority)
        queue.add(lockInfo)
        mutex?.lock()

        val subHold = subResources.map { it.take(priority) }.combine()

        return object : ResourceHold {
            override fun giveBack() {
                pollQueue(lockInfo)
                subHold.giveBack()
            }
        }
    }
}
