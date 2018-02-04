package org.fossasia.openevent.config.strategies;

import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.config.ConfigStrategy;

import lombok.Getter;

/**
 * Configures and provides Leak Canary Reference Watcher
 * To be used via {@link org.fossasia.openevent.config.StrategyRegistry}
 */
@Getter
public class LeakCanaryStrategy implements ConfigStrategy {

    private RefWatcher refWatcher;

    @Override
    public boolean configure(Context context) {
        if (LeakCanary.isInAnalyzerProcess(context)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return true;
        }
        refWatcher = LeakCanary.install((OpenEventApp) context);

        return false;
    }

}
