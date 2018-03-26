package org.fossasia.openevent.common.utils

import android.os.Looper

import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit

import timber.log.Timber

class CommonEventLoop {
    private val m_executor = Executors.newSingleThreadScheduledExecutor { r -> LooperThread(r) }

    fun post(call: Runnable) {
        m_executor.execute(call)

    }

    fun cancel(f: Future<*>) {
        if (f.isCancelled) {
            return
        }
        if (f.isDone) {
            return
        }
        f.cancel(true)
    }

    fun delayPost(call: Runnable, nMillSec: Int): Future<*> {
        return m_executor.schedule(call, nMillSec.toLong(), TimeUnit.MILLISECONDS)
    }

    //    public void waitTask(Future f) {
    //
    //    }

    fun shutdown() {
        m_executor.shutdown()
    }

    /**
     * Use a Handler Thread.
     */
    internal inner class LooperThread(private val runnable: Runnable) : Thread("CommonEventLoop") {

        override fun run() {
            Looper.prepare()
            try {
                runnable.run()
            } catch (e: Exception) {
                Timber.e("Parsing Error Occurred at CommonEventLoop::run.")
            }

        }
    }
}
