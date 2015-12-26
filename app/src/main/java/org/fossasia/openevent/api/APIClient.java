package org.fossasia.openevent.api;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import org.fossasia.openevent.api.network.OpenEventAPI;

import java.util.concurrent.TimeUnit;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

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

    static final Gson gson = new Gson();

    private final OpenEventAPI openEventAPI;

    public APIClient() {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

        RestAdapter adapter = new RestAdapter.Builder()
                .setConverter(new GsonConverter(gson))
                .setEndpoint(Urls.BASE_GET_URL_ALT)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        openEventAPI = adapter.create(OpenEventAPI.class);
    }

    public OpenEventAPI getOpenEventAPI() {
        return openEventAPI;
    }
}
