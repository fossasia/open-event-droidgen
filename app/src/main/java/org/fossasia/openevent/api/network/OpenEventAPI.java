package org.fossasia.openevent.api.network;

import org.fossasia.openevent.api.protocol.*;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * User: mohit
 * Date: 25/5/15
 */
public interface OpenEventAPI {


    @GET("/out/{id}/speakers")
    void getSpeakers(@Path("id") int id, Callback<SpeakerResponseList> speakerResponseListCallback);

    @GET("/out/{id}/sponsors.json")
    void getSponsors(@Path("id") int id, Callback<SponsorResponseList> speakerResponseListCallback);

    @GET("/out/{id}/sessions")
    void getSessions(@Path("id") int id, Callback<SessionResponseList> sessionResponseListCallback);

    //TODO:Correct event api url to server's
    @GET("/out/event")
    void getEvents(Callback<EventResponseList> eventResponseListCallback);

    @GET("/out/{id}/microlocations.json")
    void getMicrolocations(@Path("id") int id, Callback<MicrolocationResponseList> microlocationResponseListCallback);

    @GET("/out/{id}/tracks")
    void getTracks(@Path("id") int id, Callback<TrackResponseList> trackResponseListCallback);

    @GET("/out/{id}/version")
    void getVersion(@Path("id") int id, Callback<VersionResponseList> versionResponseListCallback);
}
