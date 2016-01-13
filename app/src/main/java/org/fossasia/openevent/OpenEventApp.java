package org.fossasia.openevent;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

import org.fossasia.openevent.Receivers.NetworkConnectivityChangeReceiver;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.modules.MapModuleFactory;

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
        DbSingleton.init(this);
        mapModuleFactory = new MapModuleFactory();
        registerReceiver(new NetworkConnectivityChangeReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public MapModuleFactory getMapModuleFactory() {
        return mapModuleFactory;
    }
}
