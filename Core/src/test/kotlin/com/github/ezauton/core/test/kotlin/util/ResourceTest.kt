package com.github.ezauton.core.test.kotlin.util

import com.github.ezauton.core.action.require.BaseResource
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.concurrent.thread

class ResourceTest {

    @Test
    fun `test resource taking`() {
        val resource1 = BaseResource()
        val resource2 = BaseResource()

        val resource3 = BaseResource()

        resource1.dependOn(resource3)

        val resource = BaseResource()
                .dependOn(resource1)
                .dependOn(resource2)

        var list = ArrayList<Boolean>()

        thread {
            list.add( !resource1.isTaken)
            list.add(!resource3.isTaken)

            resource1.take()

            list.add( resource1.isTaken)
            list.add(resource3.isTaken)
            list.add(resource.isTaken)
            list.add(!resource2.isTaken)

            resource1.give()
            val toAdd = arrayOf(resource, resource1, resource2, resource3).map { !it.isTaken }
            list.addAll(toAdd)

        }.join()

        list.forEachIndexed{ i, bool ->
            Assertions.assertTrue(bool, i.toString()) }

//        Assertions.assertTrue(a)
    }
}
