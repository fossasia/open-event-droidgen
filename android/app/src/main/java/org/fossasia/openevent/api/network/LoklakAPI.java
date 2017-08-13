package org.fossasia.openevent.api.network;

import org.fossasia.openevent.data.feed.LoklakFeed;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by rohanagarwal94 on 19/7/17.
 */

public interface LoklakAPI {

    @GET("/api/search.json")
    Observable<LoklakFeed> getTwitterFeed(@Query("q") String query, @Query("count") int count, @Query("source") String source);

}