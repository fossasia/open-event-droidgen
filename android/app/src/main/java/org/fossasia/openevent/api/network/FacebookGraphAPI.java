package org.fossasia.openevent.api.network;

import org.fossasia.openevent.data.feed.FacebookPageId;
import org.fossasia.openevent.data.feed.FacebookFeed;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by rohanagarwal94 on 10/6/17.
 */
public interface FacebookGraphAPI {

    @GET("/{event_name}")
    Observable<FacebookPageId> getPageId(@Path("event_name") String eventName, @Query("access_token") String accessToken);

    @GET("/{page_id}/feed")
    Observable<FacebookFeed> getPosts(@Path("page_id") String pageId, @Query("fields") String fields, @Query("access_token") String accessToken);

}
