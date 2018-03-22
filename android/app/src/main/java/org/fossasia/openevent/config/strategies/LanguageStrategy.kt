package org.fossasia.openevent.config.strategies

import android.content.Context
import org.fossasia.openevent.config.ConfigStrategy
import java.util.*

/**
 * Sets and provides default system language
 * To be used via [org.fossasia.openevent.config.StrategyRegistry]
 */
class LanguageStrategy : ConfigStrategy {

    var defaultSystemLanguage: String? = null

    override fun configure(context: Context): Boolean {
        defaultSystemLanguage = Locale.getDefault().displayLanguage
        return false
    }

}
