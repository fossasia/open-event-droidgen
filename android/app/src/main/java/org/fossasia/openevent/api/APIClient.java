package org.fossasia.openevent.api;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.fossasia.openevent.BuildConfig;
import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.api.network.FacebookGraphAPI;
import org.fossasia.openevent.api.network.OpenEventAPI;
import org.fossasia.openevent.utils.NetworkUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import timber.log.Timber;

/**
 * User: mohit
 * Date: 25/5/15
 */
public final class APIClient {
    /**
     * This is the base url can be changed via a config Param
     * Or Build Config
     */

    private static final int CONNECT_TIMEOUT_MILLIS = 20 * 1000; // 15s

    private static final int READ_TIMEOUT_MILLIS = 50 * 1000; // 20s

    private static final String CACHE_CONTROL = "Cache-Control";

    private static OpenEventAPI openEventAPI;
    private static FacebookGraphAPI facebookGraphAPI;

    private static OkHttpClient.Builder okHttpClientBuilder;
    private static Retrofit.Builder retrofitBuilder;

    static {
        okHttpClientBuilder = new OkHttpClient().newBuilder()
                .connectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

        if (BuildConfig.DEBUG)
            okHttpClientBuilder.addNetworkInterceptor(new StethoInterceptor());

        retrofitBuilder = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)));
    }

    public static OpenEventAPI getOpenEventAPI() {
        if (openEventAPI == null) {
            OkHttpClient okHttpClient = okHttpClientBuilder.addInterceptor(new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BASIC))
                    .build();

            retrofitBuilder.client(okHttpClient);

            openEventAPI = retrofitBuilder
                    .baseUrl(Urls.BASE_URL)
                    .build()
                    .create(OpenEventAPI.class);
        }

        return openEventAPI;
    }

    public static FacebookGraphAPI getFacebookGraphAPI() {
        if (facebookGraphAPI == null) {
            OkHttpClient okHttpClient = okHttpClientBuilder.addInterceptor(new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BASIC))
                    .addInterceptor(provideOfflineCacheInterceptor())
                    .addNetworkInterceptor(provideCacheInterceptor())
                    .cache(provideCache())
                    .build();

            retrofitBuilder.client(okHttpClient);

            facebookGraphAPI = retrofitBuilder
                    .baseUrl(Urls.FACEBOOK_BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
                    .create(FacebookGraphAPI.class);
        }

        return facebookGraphAPI;
    }

    private static Cache provideCache() {
        Cache cache = null;
        try {
            cache = new Cache(new File(OpenEventApp.getAppContext().getCacheDir(), "facebook-feed-cache"),
                    10 * 1024 * 1024); // 10 MB
        } catch (Exception e) {
            Timber.e(e, "Could not create Cache!");
        }
        return cache;
    }

    private static Interceptor provideCacheInterceptor() {
        return chain -> {
            Response response = chain.proceed(chain.request());

            // re-write response header to force use of cache
            CacheControl cacheControl = new CacheControl.Builder()
                    .maxAge(2, TimeUnit.MINUTES)
                    .build();

            return response.newBuilder()
                    .removeHeader("Pragma")
                    .header(CACHE_CONTROL, cacheControl.toString())
                    .build();
        };
    }

    private static Interceptor provideOfflineCacheInterceptor() {
        return chain -> {
            Request request = chain.request();

            if (!NetworkUtils.haveNetworkConnection(OpenEventApp.getAppContext())) {
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build();

                request = request.newBuilder()
                        .removeHeader("Pragma")
                        .cacheControl(cacheControl)
                        .build();
            }

            return chain.proceed(request);
        };
    }
}
