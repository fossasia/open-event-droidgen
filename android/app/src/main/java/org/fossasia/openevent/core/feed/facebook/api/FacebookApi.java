package org.fossasia.openevent.core.feed.facebook.api;

import org.fossasia.openevent.common.api.APIClient;
import org.fossasia.openevent.common.api.Urls;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class FacebookApi {

    private static FacebookGraphAPI facebookGraphAPI;

    public static FacebookGraphAPI getFacebookGraphAPI() {
        Retrofit.Builder retrofitBuilder = APIClient.getRetrofitBuilder();
        if (facebookGraphAPI == null) {
            retrofitBuilder.client(APIClient.getClient());

            facebookGraphAPI = retrofitBuilder
                    .baseUrl(Urls.FACEBOOK_BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
                    .create(FacebookGraphAPI.class);
        }

        return facebookGraphAPI;
    }

}
