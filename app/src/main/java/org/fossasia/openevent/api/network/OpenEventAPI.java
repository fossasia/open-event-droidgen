package org.fossasia.openevent.api.network;

import org.fossasia.openevent.api.protocol.EventResponseList;
import org.fossasia.openevent.api.protocol.MicrolocationResponseList;
import org.fossasia.openevent.api.protocol.SessionResponseList;
import org.fossasia.openevent.api.protocol.SpeakerResponseList;
import org.fossasia.openevent.api.protocol.SponsorResponseList;
import org.fossasia.openevent.api.protocol.TrackResponseList;
import org.fossasia.openevent.api.protocol.VersionResponseList;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * User: mohit
 * Date: 25/5/15
 */
public interface OpenEventAPI {


    @GET("/event/{id}/speakers")
    void getSpeakers(@Path("id") int id, Callback<SpeakerResponseList> speakerResponseListCallback);

    @GET("/event/{id}/sponsors")
    void getSponsors(@Path("id") int id, Callback<SponsorResponseList> speakerResponseListCallback);

    @GET("/event/{id}/sessions")
    void getSessions(@Path("id") int id, Callback<SessionResponseList> sessionResponseListCallback);

    //TODO:Correct event api url to server's
    @GET("/event/event")
    void getEvents(Callback<EventResponseList> eventResponseListCallback);

    @GET("/event/{id}/microlocations")
    void getMicrolocations(@Path("id") int id, Callback<MicrolocationResponseList> microlocationResponseListCallback);

    @GET("/event/{id}/tracks")
    void getTracks(@Path("id") int id, Callback<TrackResponseList> trackResponseListCallback);

    @GET("/event/{id}/version")
    void getVersion(@Path("id") int id, Callback<VersionResponseList> versionResponseListCallback);
}
