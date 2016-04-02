package org.fossasia.openevent.api.network;

import org.fossasia.openevent.api.protocol.EventResponseList;
import org.fossasia.openevent.api.protocol.MicrolocationResponseList;
import org.fossasia.openevent.api.protocol.SessionResponseList;
import org.fossasia.openevent.api.protocol.SpeakerResponseList;
import org.fossasia.openevent.api.protocol.SponsorResponseList;
import org.fossasia.openevent.api.protocol.TrackResponseList;
import org.fossasia.openevent.api.protocol.VersionResponseList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * User: mohit
 * Date: 25/5/15
 */
public interface OpenEventAPI {

    @GET("event/{id}/speakers")
    Call<SpeakerResponseList> getSpeakers(@Path("id") int id);

    @GET("event/{id}/sponsors")
    Call<SponsorResponseList> getSponsors(@Path("id") int id);

    @GET("event/{id}/sessions")
    Call<SessionResponseList> getSessions(@Path("id") int id);

    //TODO:Correct event api url to server's
    @GET("event/event")
    Call<EventResponseList> getEvents();

    @GET("event/{id}/microlocations")
    Call<MicrolocationResponseList> getMicrolocations(@Path("id") int id);

    @GET("event/{id}/tracks")
    Call<TrackResponseList> getTracks(@Path("id") int id);

    //https://raw.githubusercontent.com/fossasia/open-event/master/testapi/event/1/version
    @GET("event/{id}/version")
    Call<VersionResponseList> getVersion(@Path("id") int id);
}
