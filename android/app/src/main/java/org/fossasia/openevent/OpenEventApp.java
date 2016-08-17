package org.fossasia.openevent;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.fossasia.openevent.Receivers.NetworkConnectivityChangeReceiver;
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.ConnectionCheckEvent;
import org.fossasia.openevent.events.DataDownloadEvent;
import org.fossasia.openevent.events.ShowNetworkDialogEvent;
import org.fossasia.openevent.modules.MapModuleFactory;
import org.fossasia.openevent.utils.ConstantStrings;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import timber.log.Timber;

/**
 * User: MananWason
 * Date: 02-06-2015
 */
public class OpenEventApp extends Application {

    static Handler handler;

    private static Bus eventBus;

    MapModuleFactory mapModuleFactory;

    SharedPreferences sharedPreferences;

    private static Context context;

    public static Bus getEventBus() {
        if (eventBus == null) {
            eventBus = new Bus();
        }
        return eventBus;
    }

    public static void postEventOnUIThread(final Object event) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                getEventBus().post(event);
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        OpenEventApp.context = getApplicationContext();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getAppContext());

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
        DbSingleton.init(this);
        mapModuleFactory = new MapModuleFactory();
        registerReceiver(new NetworkConnectivityChangeReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        getEventBus().register(this);

        String json = null;
        try {
            InputStream inputStream = getAssets().open("config.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonObject = new JSONObject(json);
            String email = jsonObject.getString("Email");
            String app_name = jsonObject.getString("App_Name");
            String api_link = jsonObject.getString("Api_Link");

            Urls.setBaseUrl(api_link);

            sharedPreferences.edit().putString(ConstantStrings.EMAIL, email).apply();
            sharedPreferences.edit().putString(ConstantStrings.APP_NAME, app_name).apply();
            sharedPreferences.edit().putString(ConstantStrings.BASE_API_URL, api_link).apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public static Context getAppContext() {
        return OpenEventApp.context;
    }


    @Subscribe
    public void onConnectionChangeReact(ConnectionCheckEvent event) {
        if (event.connState()) {
            postEventOnUIThread(new DataDownloadEvent());
            Timber.d("[NetNotif] %s", "Connected to Internet");
        } else {
            Timber.d("[NetNotif] %s", "Not connected to Internet");
            postEventOnUIThread(new ShowNetworkDialogEvent());
        }
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
