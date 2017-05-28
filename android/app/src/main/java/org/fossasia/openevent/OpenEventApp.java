package org.fossasia.openevent;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.fossasia.openevent.activities.MainActivity;
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.ConnectionCheckEvent;
import org.fossasia.openevent.events.ShowNetworkDialogEvent;
import org.fossasia.openevent.modules.MapModuleFactory;
import org.fossasia.openevent.receivers.NetworkConnectivityChangeReceiver;
import org.fossasia.openevent.utils.ConstantStrings;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Locale;

import io.branch.referral.Branch;
import okhttp3.OkHttpClient;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 02-06-2015
 */
public class OpenEventApp extends Application {

    public static final String API_LINK = "Api_Link";
    public static final String EMAIL = "Email";
    public static final String APP_NAME = "App_Name";

    public static String sDefSystemLanguage;
    private static Handler handler;
    private static Bus eventBus;
    private static WeakReference<Context> context;
    private MapModuleFactory mapModuleFactory;
    private RefWatcher refWatcher;

    public static Bus getEventBus() {
        if (eventBus == null) {
            eventBus = new Bus();
        }
        return eventBus;
    }

    public static void postEventOnUIThread(final Object event) {
        handler.post(() -> getEventBus().post(event));
    }

    public static Context getAppContext() {
        return context.get();
    }

    public static RefWatcher getRefWatcher(Context context) {
        OpenEventApp application = (OpenEventApp) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        context = new WeakReference<>(getApplicationContext());

        Branch.getAutoInstance(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getAppContext());
        sDefSystemLanguage = Locale.getDefault().getDisplayLanguage();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        refWatcher = LeakCanary.install(this);

        if (BuildConfig.DEBUG) {
            // Create an InitializerBuilder
            Stetho.initializeWithDefaults(getApplicationContext());

            //Initialize Stetho Interceptor into OkHttp client
            OkHttpClient httpClient = new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).build();

            //Initialize Picasso
            Picasso picasso = new Picasso.Builder(this).downloader(new OkHttp3Downloader(httpClient)).build();
            Picasso.setSingletonInstance(picasso);

            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

        DbSingleton.init(this);
        mapModuleFactory = new MapModuleFactory();
        registerReceiver(new NetworkConnectivityChangeReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        getEventBus().register(this);

        String config_json = null;
        String event_json = null;

        //getting config.json data
        try {
            InputStream inputStream = getAssets().open("config.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            config_json = new String(buffer, "UTF-8");
        } catch(IOException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(config_json);
            String email = jsonObject.has(EMAIL) ? jsonObject.getString(EMAIL) : "";
            String app_name = jsonObject.has(APP_NAME) ? jsonObject.getString(APP_NAME) : "";
            String api_link = jsonObject.has(API_LINK) ? jsonObject.getString(API_LINK) : "";

            Urls.setBaseUrl(api_link);

            sharedPreferences.edit().putString(ConstantStrings.EMAIL, email).apply();
            sharedPreferences.edit().putString(ConstantStrings.APP_NAME, app_name).apply();
            sharedPreferences.edit().putString(ConstantStrings.BASE_API_URL, api_link).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //getting event data
        try {
            InputStream inputStream = getAssets().open("event");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            event_json = new String(buffer, "UTF-8");
        } catch(IOException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(event_json);
            String org_description = jsonObject.has(ConstantStrings.ORG_DESCRIPTION) ?
                    jsonObject.getString(ConstantStrings.ORG_DESCRIPTION) : "";
            sharedPreferences.edit().putString(ConstantStrings.ORG_DESCRIPTION, org_description).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Subscribe
    public void onConnectionChangeReact(ConnectionCheckEvent event) {
        if (event.connState()) {
            Timber.d("[NetNotif] %s", "Connected to Internet");

            if(MainActivity.dialogNetworkNotiff != null)
                MainActivity.dialogNetworkNotiff.dismiss();

        } else {
            Timber.d("[NetNotif] %s", "Not connected to Internet");
            postEventOnUIThread(new ShowNetworkDialogEvent());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        sDefSystemLanguage = newConfig.locale.getDisplayLanguage();
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
