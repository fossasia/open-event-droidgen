package org.fossasia.openevent.config.strategies

import android.content.Context
import org.fossasia.openevent.config.ConfigStrategy
import org.fossasia.openevent.core.location.modules.MapModuleFactory

/**
 * Configures and provides Map Module Factory to switch map implementations between flavours
 * To be used via [org.fossasia.openevent.config.StrategyRegistry]
 */
class MapModuleStrategy : ConfigStrategy {

    var mapModuleFactory: MapModuleFactory? = null
        private set

    override fun configure(context: Context): Boolean {
        mapModuleFactory = MapModuleFactory()
        return false
    }

}
