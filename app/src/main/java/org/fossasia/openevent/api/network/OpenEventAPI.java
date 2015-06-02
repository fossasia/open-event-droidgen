package org.fossasia.openevent.api.network;

import org.fossasia.openevent.api.protocol.EventResponseList;
import org.fossasia.openevent.api.protocol.MicrolocationResponseList;
import org.fossasia.openevent.api.protocol.SessionResponseList;
import org.fossasia.openevent.api.protocol.SpeakerResponseList;
import org.fossasia.openevent.api.protocol.SponsorResponseList;
import org.fossasia.openevent.api.protocol.TrackResponseList;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * User: mohit
 * Date: 25/5/15
 */
public interface OpenEventAPI {


    //TODO: Fix Hardcoding here
    @GET("/event/{id}/speakers")
    void getSpeakers(@Path("id") int id, Callback<SpeakerResponseList> speakerResponseListCallback);

    @GET("/event/{id}/sponsors")
    void getSponsors(@Path("id") int id, Callback<SponsorResponseList> speakerResponseListCallback);

    @GET("/event/{id}/sessions")
    void getSessions(@Path("id") int id, Callback<SessionResponseList> sessionResponseListCallback);

    @GET("/event/{id}")
    void getEvents(@Path("id") int id, Callback<EventResponseList> eventResponseListCallback);

    @GET("/event/{id}/microlocations")
    void getMicrolocations(@Path("id") int id, Callback<MicrolocationResponseList> microlocationResponseListCallback);

    @GET("/event/{id}/tracks")
    void getTracks(@Path("id") int id, Callback<TrackResponseList> trackResponseListCallback);
}
