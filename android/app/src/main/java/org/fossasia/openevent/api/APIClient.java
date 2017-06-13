package org.fossasia.openevent.api;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.fossasia.openevent.BuildConfig;
import org.fossasia.openevent.api.network.FacebookGraphAPI;
import org.fossasia.openevent.api.network.OpenEventAPI;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

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

    private static OpenEventAPI openEventAPI;
    private static FacebookGraphAPI facebookGraphAPI;

    private static Retrofit.Builder retrofitBuilder;

    static {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient().newBuilder()
                .connectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

        if (BuildConfig.DEBUG)
            okHttpClientBuilder.addNetworkInterceptor(new StethoInterceptor());

        OkHttpClient okHttpClient = okHttpClientBuilder.addInterceptor(new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build();

        retrofitBuilder = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)))
                .client(okHttpClient);
    }

    public static OpenEventAPI getOpenEventAPI() {
        if (openEventAPI == null)
            openEventAPI = retrofitBuilder
                    .baseUrl(Urls.BASE_URL)
                    .build()
                    .create(OpenEventAPI.class);

        return openEventAPI;
    }

    public static FacebookGraphAPI getFacebookGraphAPI() {
        if (facebookGraphAPI == null)
            facebookGraphAPI = retrofitBuilder
                    .baseUrl(Urls.FACEBOOK_BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
                    .create(FacebookGraphAPI.class);

        return facebookGraphAPI;
    }

}
