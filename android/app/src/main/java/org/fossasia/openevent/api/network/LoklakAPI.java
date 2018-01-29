package org.fossasia.openevent.api.network;

import org.fossasia.openevent.data.twitter.TwitterFeed;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LoklakAPI {

    @GET("/api/search.json")
    Observable<TwitterFeed> getTwitterFeed(@Query("q") String query, @Query("count") int count, @Query("source") String source);

}
