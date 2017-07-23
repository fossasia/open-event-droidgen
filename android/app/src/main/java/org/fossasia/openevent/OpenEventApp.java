package org.fossasia.openevent;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import org.fossasia.openevent.activities.MainActivity;
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.dbutils.RealmDatabaseMigration;
import org.fossasia.openevent.events.ConnectionCheckEvent;
import org.fossasia.openevent.events.ShowNetworkDialogEvent;
import org.fossasia.openevent.modules.MapModuleFactory;
import org.fossasia.openevent.receivers.NetworkConnectivityChangeReceiver;
import org.fossasia.openevent.utils.ConstantStrings;
import org.fossasia.openevent.utils.DateConverter;
import org.fossasia.openevent.utils.SharedPreferencesUtil;
import org.fossasia.openevent.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Locale;

import io.branch.referral.Branch;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.Cache;
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
    public static Picasso picassoWithCache;
    private static ObjectMapper objectMapper;
    private MapModuleFactory mapModuleFactory;
    private RefWatcher refWatcher;
    private MainActivity activity;

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

    public static ObjectMapper getObjectMapper(){
        if (objectMapper == null){
            objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
        return objectMapper;
    }

    public void setUpTimeZone() {
        DateConverter.setShowLocalTimeZone(SharedPreferencesUtil.getBoolean(getResources()
                .getString(R.string.timezone_mode_key), false));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        context = new WeakReference<>(getApplicationContext());

        Branch.getAutoInstance(this);

        setUpTimeZone();
        sDefSystemLanguage = Locale.getDefault().getDisplayLanguage();

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(RealmDatabaseMigration.DB_VERSION) // Must be bumped when the schema changes
                //TODO: Re-add migration once DB is locked/finilized
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(config);

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        refWatcher = LeakCanary.install(this);

        //Initialize Cache
        File httpCacheDirectory = new File(getCacheDir(), "picasso-cache");
        Cache cache = new Cache(httpCacheDirectory, 15 * 1024 * 1024);

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder().cache(cache);

        if (BuildConfig.DEBUG) {
            // Create an InitializerBuilder
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                            .build());

            //Initialize Stetho Interceptor into OkHttp client
            OkHttpClient httpClient = new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor()).build();
            okHttpClientBuilder = okHttpClientBuilder.addNetworkInterceptor(new StethoInterceptor());

            //Initialize Picasso
            Picasso picasso = new Picasso.Builder(this).downloader(new OkHttp3Downloader(httpClient)).build();
            Picasso.setSingletonInstance(picasso);

            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

        //Initialize Picasso with cache
        picassoWithCache = new Picasso.Builder(this).downloader(new OkHttp3Downloader(okHttpClientBuilder.build())).build();

        mapModuleFactory = new MapModuleFactory();
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

            SharedPreferencesUtil.putString(ConstantStrings.EMAIL, email);
            SharedPreferencesUtil.putString(ConstantStrings.APP_NAME, app_name);
            SharedPreferencesUtil.putString(ConstantStrings.BASE_API_URL, api_link);
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
            String eventTimeZone = jsonObject.has(ConstantStrings.TIMEZONE) ? jsonObject.getString(ConstantStrings.TIMEZONE) : "";
            SharedPreferencesUtil.putString(ConstantStrings.ORG_DESCRIPTION, org_description);
            SharedPreferencesUtil.putString(ConstantStrings.TIMEZONE, eventTimeZone);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!Utils.isBaseUrlEmpty()) {
            registerReceiver(new NetworkConnectivityChangeReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    public void attachMainActivity(MainActivity activity) {
        this.activity = activity;
    }

    public void detachMainActivity() {
        this.activity = null;
    }

    @Subscribe
    public void onConnectionChangeReact(ConnectionCheckEvent event) {
        if (event.connState()) {
            Timber.d("[NetNotif] %s", "Connected to Internet");

            if(activity != null)
                activity.dismissDialogNetworkNotification();

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
