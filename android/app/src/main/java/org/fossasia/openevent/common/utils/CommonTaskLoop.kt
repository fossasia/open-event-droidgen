package org.fossasia.openevent.common.utils

class CommonTaskLoop private constructor() {

    private val commonEventLoop: CommonEventLoop = CommonEventLoop()

    fun post(call: Runnable) {
        commonEventLoop.post(call)
    }

    fun delayPost(call: Runnable, nMillSec: Int) {
        commonEventLoop.delayPost(call, nMillSec)
    }

    fun shutdown() {
        commonEventLoop.shutdown()
    }

    companion object {
        @JvmStatic
        val instance = CommonTaskLoop()
    }
}
