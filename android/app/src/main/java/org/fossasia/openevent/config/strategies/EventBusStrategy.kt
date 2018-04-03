package org.fossasia.openevent.config.strategies

import android.content.Context
import android.os.Handler
import android.os.Looper

import com.squareup.otto.Bus

import org.fossasia.openevent.config.ConfigStrategy

/**
 * Configures and provides EventBus singleton for use throughout app
 *
 * To be used via [org.fossasia.openevent.config.StrategyRegistry]
 *
 * TODO: Remove event bus from project
 */
class EventBusStrategy : ConfigStrategy {

    var eventBus: Bus? = null
        get() {
            if (field == null) {
                field = Bus()
            }
            return field
        }

    private val handler: Handler = Handler(Looper.getMainLooper())

    override fun configure(context: Context): Boolean {
        eventBus?.register(context)
        return false
    }

    fun postEventOnUIThread(event: Any) {
        handler.post { eventBus?.post(event) }
    }

}
