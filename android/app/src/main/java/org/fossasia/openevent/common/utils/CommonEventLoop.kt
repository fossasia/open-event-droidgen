package org.fossasia.openevent.common.utils

import android.os.Looper

import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit

import timber.log.Timber

class CommonEventLoop {
    private val executor = Executors.newSingleThreadScheduledExecutor { runnable -> LooperThread(runnable) }

    fun post(call: Runnable) {
        executor.execute(call)
    }

    fun cancel(future: Future<*>) {
        if (!(future.isCancelled || future.isDone)) future.cancel(true)
    }

    fun delayPost(call: Runnable, millSec: Int): Future<*> {
        return executor.schedule(call, millSec.toLong(), TimeUnit.MILLISECONDS)
    }

    fun shutdown() {
        executor.shutdown()
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
                Timber.e("Parsing Error Occurred at CommonEventLoop::run. $e.")
            }

        }
    }
}
