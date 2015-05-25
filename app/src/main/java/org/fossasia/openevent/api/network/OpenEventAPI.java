package org.fossasia.openevent.api.network;

import org.fossasia.openevent.api.protocol.SpeakerResponseList;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * User: mohit
 * Date: 25/5/15
 */
public interface OpenEventAPI {

    @GET("/speakers")
    void getSpeakers(Callback<SpeakerResponseList> speakerResponseListCallback);
}
