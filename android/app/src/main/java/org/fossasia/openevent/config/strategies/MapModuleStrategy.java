package org.fossasia.openevent.config.strategies;

import android.content.Context;

import org.fossasia.openevent.config.ConfigStrategy;
import org.fossasia.openevent.core.location.modules.MapModuleFactory;

import lombok.Getter;

/**
 * Configures and provides Map Module Factory to switch map implementations between flavours
 * To be used via {@link org.fossasia.openevent.config.StrategyRegistry}
 */
@Getter
public class MapModuleStrategy implements ConfigStrategy {

    private MapModuleFactory mapModuleFactory;

    @Override
    public boolean configure(Context context) {
        mapModuleFactory = new MapModuleFactory();
        return false;
    }

}
