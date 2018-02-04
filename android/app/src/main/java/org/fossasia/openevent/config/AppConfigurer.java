package org.fossasia.openevent.config;

import android.content.Context;

/**
 * Holds application context and a strategy holder and configures each strategy in order
 *
 * If any strategy returns true to halt, all further strategies are skipped
 * Uses {@link StrategyRegistry} singleton to get default holder if not provided
 */
public class AppConfigurer {

    private final Context context;
    private final ConfigStrategyHolder configStrategyHolder;

    public AppConfigurer(Context context, ConfigStrategyHolder configStrategyHolder) {
        this.context = context;
        this.configStrategyHolder = configStrategyHolder;
    }

    public void configure() {
        for (ConfigStrategy configStrategy : configStrategyHolder.getStrategies()) {
            boolean shouldHalt = configStrategy.configure(context);
            if (shouldHalt)
                break;
        }
    }

    public static void configure(Context context, ConfigStrategyHolder configStrategyHolder) {
        new AppConfigurer(context, configStrategyHolder).configure();
    }

    public static void configure(Context context) {
        configure(context, StrategyRegistry.getInstance().getDefaultHolder());
    }

}
