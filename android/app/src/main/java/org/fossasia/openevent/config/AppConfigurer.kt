package org.fossasia.openevent.config

import android.content.Context

/**
 * Holds application context and a strategy holder and configures each strategy in order
 *
 * If any strategy returns true to halt, all further strategies are skipped
 * Uses [StrategyRegistry] singleton to get default holder if not provided
 */
class AppConfigurer(private val context: Context?, private val configStrategyHolder: ConfigStrategyHolder) {

    fun configure() {
        for (configStrategy in configStrategyHolder.strategies) {
            val shouldHalt = configStrategy.configure(context)
            if (shouldHalt)
                break
        }
    }

    companion object {

        @JvmOverloads
        @JvmStatic
        fun configure(context: Context?, configStrategyHolder: ConfigStrategyHolder = StrategyRegistry.instance.getDefaultHolder()) {
            AppConfigurer(context, configStrategyHolder).configure()
        }
    }

}
