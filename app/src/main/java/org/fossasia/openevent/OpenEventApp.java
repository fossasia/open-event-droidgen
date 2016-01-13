package org.fossasia.openevent;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.squareup.otto.Bus;

import org.fossasia.openevent.Receivers.NetworkConnectivityChangeReceiver;
import org.fossasia.openevent.activities.MainActivity;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.ErrorLogProcessEvent;
import org.fossasia.openevent.modules.MapModuleFactory;

import timber.log.Timber;

/**
 * User: MananWason
 * Date: 02-06-2015
 */
public class OpenEventApp extends Application {

    private static Bus sEventBus;
    MapModuleFactory mapModuleFactory;

    private class ErrorsTree extends Timber.Tree {

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            OpenEventApp.postEventOnUIThread(new ErrorLogProcessEvent(priority, tag, message, t));
        }
    }

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
        Timber.plant(new Timber.DebugTree());
        Timber.plant(new ErrorsTree());
    }

    public MapModuleFactory getMapModuleFactory() {
        return mapModuleFactory;
    }


}

