package org.fossasia.openevent;

import android.content.Context;
import android.content.res.Configuration;
import android.support.multidex.MultiDexApplication;

import org.fossasia.openevent.config.AppConfigurer;
import org.fossasia.openevent.config.StrategyRegistry;

import java.lang.ref.WeakReference;

import io.branch.referral.Branch;

public class OpenEventApp extends MultiDexApplication {

    private static WeakReference<Context> context;

    // TODO: Remove all instances
    public static Context getAppContext() {
        return context.get();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = new WeakReference<>(getApplicationContext());

        Branch.getAutoInstance(this);
        AppConfigurer.configure(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        StrategyRegistry.getInstance().getLanguageStrategy().setDefaultSystemLanguage(newConfig.locale.getDisplayLanguage());
    }

}
