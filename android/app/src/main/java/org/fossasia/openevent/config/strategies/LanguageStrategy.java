package org.fossasia.openevent.config.strategies;

import android.content.Context;

import org.fossasia.openevent.config.ConfigStrategy;

import java.util.Locale;

import lombok.Getter;
import lombok.Setter;

/**
 * Sets and provides default system language
 * To be used via {@link org.fossasia.openevent.config.StrategyRegistry}
 */
@Setter
@Getter
public class LanguageStrategy implements ConfigStrategy {

    private String defaultSystemLanguage;

    @Override
    public boolean configure(Context context) {
        defaultSystemLanguage = Locale.getDefault().getDisplayLanguage();
        return false;
    }

}
