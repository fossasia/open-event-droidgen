package org.fossasia.openevent.config

import org.fossasia.openevent.config.strategies.*

/**
 * Project level configuration strategies holder singleton
 *
 * Holds all strategies and provides an interface to get singleton strategies or
 * override the default strategy holder. Also sets the order and strategies in default
 * holder which is then utilised by [AppConfigurer] to configure the application
 */
class StrategyRegistry private constructor() {
    private val defaultHolder: ConfigStrategyHolder = ConfigStrategyHolder()

    var strategyHolder: ConfigStrategyHolder? = null

    private fun <T> lazyInit(field: T?, initializer: () -> T): T {
        return field ?: initializer()
    }

    var httpStrategy: HttpStrategy? = null
        get() {
            field = lazyInit(field) { HttpStrategy() }
            return field
        }
    var languageStrategy: LanguageStrategy? = null
        get() {
            field = lazyInit(field) { LanguageStrategy() }
            return field
        }
    var leakCanaryStrategy: LeakCanaryStrategy? = null
        get() {
            field = lazyInit(field) { LeakCanaryStrategy() }
            return field
        }
    var eventBusStrategy: EventBusStrategy? = null
        get() {
            field = lazyInit(field) { EventBusStrategy() }
            return field
        }
    var mapModuleStrategy: MapModuleStrategy? = null
        get() {
            field = lazyInit(field) { MapModuleStrategy() }
            return field
        }
    var appConfigStrategy: AppConfigStrategy? = null
        get() {
            field = lazyInit(field) { AppConfigStrategy() }
            return field
        }


    fun getDefaultHolder(): ConfigStrategyHolder {
        if (strategyHolder != null)
            return strategyHolder as ConfigStrategyHolder
        defaultHolder.register(leakCanaryStrategy)
        defaultHolder.register(httpStrategy)
        defaultHolder.register(eventBusStrategy)
        defaultHolder.register(mapModuleStrategy)
        defaultHolder.register(appConfigStrategy)
        defaultHolder.register(languageStrategy)
        defaultHolder.register(TimberStrategy())
        defaultHolder.register(TimeConfigStrategy())
        defaultHolder.register(RealmStrategy())
        return defaultHolder
    }

    companion object {
        @JvmStatic
        val instance by lazy { StrategyRegistry() }
    }

}
