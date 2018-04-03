package org.fossasia.openevent.config.strategies

import android.content.Context
import android.util.Log

import org.fossasia.openevent.BuildConfig
import org.fossasia.openevent.config.ConfigStrategy

import timber.log.Timber

class TimberStrategy : ConfigStrategy {

    override fun configure(context: Context): Boolean {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }

        return false
    }

    /**
     * A tree which logs important information for crash reporting.
     */
    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }

            FakeCrashLibrary.log(priority, tag, message)

            if (t != null) {
                if (priority == Log.ERROR) {
                    FakeCrashLibrary.logError(t)
                } else if (priority == Log.WARN) {
                    FakeCrashLibrary.logWarning(t)
                }
            }
        }
    }

    private class FakeCrashLibrary private constructor() {

        /**
         * Not a real crash reporting library!
         */

        init {
            throw AssertionError("No instances.")
        }

        companion object {

            fun log(priority: Int, tag: String?, message: String) {
                // TODO add log entry to circular buffer.
            }

            fun logWarning(t: Throwable) {
                // TODO report non-fatal warning.
            }

            fun logError(t: Throwable) {
                // TODO report non-fatal error.
            }
        }
    }

}
