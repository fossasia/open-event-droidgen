package org.fossasia.openevent;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.squareup.otto.Bus;

import org.fossasia.openevent.Receivers.NetworkConnectivityChangeReceiver;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.modules.MapModuleFactory;

import timber.log.Timber;

/**
 * User: MananWason
 * Date: 02-06-2015
 */
public class OpenEventApp extends Application {

    private static Bus sEventBus;

    MapModuleFactory mapModuleFactory;

    public static Bus getEventBus() {
        if (sEventBus == null) {
            sEventBus = new Bus();
        }
        return sEventBus;
    }

    public static void postEventOnUIThread(final Object event) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                getEventBus().post(event);
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

        DbSingleton.init(this);
        mapModuleFactory = new MapModuleFactory();
        registerReceiver(new NetworkConnectivityChangeReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public MapModuleFactory getMapModuleFactory() {
        return mapModuleFactory;
    }

    /**
     * A tree which logs important information for crash reporting.
     */
    private static class CrashReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            FakeCrashLibrary.log(priority, tag, message);

            if (t != null) {
                if (priority == Log.ERROR) {
                    FakeCrashLibrary.logError(t);
                } else if (priority == Log.WARN) {
                    FakeCrashLibrary.logWarning(t);
                }
            }
        }
    }
}
