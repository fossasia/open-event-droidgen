package org.fossasia.openevent.core.feed.twitter.api;

import org.fossasia.openevent.common.api.APIClient;
import org.fossasia.openevent.common.api.Urls;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class TwitterApi {

    private static LoklakAPI loklakAPI;

    public static LoklakAPI getLoklakAPI() {
        if (loklakAPI == null) {
            Retrofit.Builder retrofitBuilder = APIClient.getRetrofitBuilder();
            retrofitBuilder.client(APIClient.getClient());

            loklakAPI = retrofitBuilder.baseUrl(Urls.LOKLAK_BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
                    .create(LoklakAPI.class);
        }

        return loklakAPI;
    }

}
