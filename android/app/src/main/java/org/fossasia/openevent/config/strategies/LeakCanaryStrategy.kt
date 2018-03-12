package org.fossasia.openevent.config.strategies

import android.content.Context

import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher

import org.fossasia.openevent.OpenEventApp
import org.fossasia.openevent.config.ConfigStrategy

/**
 * Configures and provides Leak Canary Reference Watcher
 * To be used via [org.fossasia.openevent.config.StrategyRegistry]
 */
class LeakCanaryStrategy : ConfigStrategy {

    var refWatcher: RefWatcher? = null
        private set

    override fun configure(context: Context): Boolean {
        if (LeakCanary.isInAnalyzerProcess(context)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return true
        }
        refWatcher = LeakCanary.install(context as OpenEventApp)

        return false
    }

}
