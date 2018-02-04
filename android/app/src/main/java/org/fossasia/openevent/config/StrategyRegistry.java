package org.fossasia.openevent.config;

import org.fossasia.openevent.config.strategies.AppConfigStrategy;
import org.fossasia.openevent.config.strategies.EventBusStrategy;
import org.fossasia.openevent.config.strategies.HttpStrategy;
import org.fossasia.openevent.config.strategies.LanguageStrategy;
import org.fossasia.openevent.config.strategies.LeakCanaryStrategy;
import org.fossasia.openevent.config.strategies.MapModuleStrategy;
import org.fossasia.openevent.config.strategies.RealmStrategy;
import org.fossasia.openevent.config.strategies.TimberStrategy;
import org.fossasia.openevent.config.strategies.TimeConfigStrategy;

import lombok.Setter;

/**
 * Project level configuration strategies holder singleton
 *
 * Holds all strategies and provides an interface to get singleton strategies or
 * override the default strategy holder. Also sets the order and strategies in default
 * holder which is then utilised by {@link AppConfigurer} to configure the application
 */
@Setter
public class StrategyRegistry {

    private static StrategyRegistry strategyRegistry;
    private final ConfigStrategyHolder defaultHolder;

    private ConfigStrategyHolder strategyHolder;

    private HttpStrategy httpStrategy;
    private LanguageStrategy languageStrategy;
    private LeakCanaryStrategy leakCanaryStrategy;
    private EventBusStrategy eventBusStrategy;
    private MapModuleStrategy mapModuleStrategy;
    private AppConfigStrategy appConfigStrategy;

    private StrategyRegistry() {
        // Prevent anyone from creating a new instance
        defaultHolder = new ConfigStrategyHolder();
    }

    public static StrategyRegistry getInstance() {
        if (strategyRegistry == null)
            strategyRegistry = new StrategyRegistry();

        return strategyRegistry;
    }

    public HttpStrategy getHttpStrategy() {
        if (httpStrategy == null)
            httpStrategy = new HttpStrategy();

        return httpStrategy;
    }

    public LeakCanaryStrategy getLeakCanaryStrategy() {
        if (leakCanaryStrategy == null)
            leakCanaryStrategy = new LeakCanaryStrategy();
        return leakCanaryStrategy;
    }

    public LanguageStrategy getLanguageStrategy() {
        if (languageStrategy == null)
            languageStrategy = new LanguageStrategy();

        return languageStrategy;
    }

    public EventBusStrategy getEventBusStrategy() {
        if (eventBusStrategy == null)
            eventBusStrategy = new EventBusStrategy();
        return eventBusStrategy;
    }

    public MapModuleStrategy getMapModuleStrategy() {
        if (mapModuleStrategy == null)
            mapModuleStrategy = new MapModuleStrategy();
        return mapModuleStrategy;
    }

    public AppConfigStrategy getAppConfigStrategy() {
        if (appConfigStrategy == null)
            appConfigStrategy = new AppConfigStrategy();
        return appConfigStrategy;
    }

    public ConfigStrategyHolder getDefaultHolder() {
        if (strategyHolder != null)
            return strategyHolder;
        defaultHolder.register(getLeakCanaryStrategy());
        defaultHolder.register(getHttpStrategy());
        defaultHolder.register(getEventBusStrategy());
        defaultHolder.register(getMapModuleStrategy());
        defaultHolder.register(getAppConfigStrategy());
        defaultHolder.register(getLanguageStrategy());
        defaultHolder.register(new TimberStrategy());
        defaultHolder.register(new TimeConfigStrategy());
        defaultHolder.register(new RealmStrategy());
        return defaultHolder;
    }

}
