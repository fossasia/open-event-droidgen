package org.fossasia.openevent.api;

import org.fossasia.openevent.api.network.OpenEventAPI;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * User: mohit
 * Date: 25/5/15
 */
public final class APIClient {
    /**
     * This is the base url can be changed via a config Param
     * Or Build Config
     */

    static final int CONNECT_TIMEOUT_MILLIS = 20 * 1000; // 15s

    static final int READ_TIMEOUT_MILLIS = 50 * 1000; // 20s

    private final OpenEventAPI openEventAPI;

    public APIClient() {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .addInterceptor(new HttpLoggingInterceptor().
                        setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Urls.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        openEventAPI = retrofit.create(OpenEventAPI.class);
    }

    public OpenEventAPI getOpenEventAPI() {

        return openEventAPI;
    }


}
