package org.fossasia.openevent.api.network;

import org.fossasia.openevent.api.protocol.EventResponseList;
import org.fossasia.openevent.api.protocol.MicrolocationResponseList;
import org.fossasia.openevent.api.protocol.SessionResponseList;
import org.fossasia.openevent.api.protocol.SpeakerResponseList;
import org.fossasia.openevent.api.protocol.SponsorResponseList;
import org.fossasia.openevent.api.protocol.TrackResponseList;
import org.fossasia.openevent.data.Sponsor;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * User: mohit
 * Date: 25/5/15
 */
public interface OpenEventAPI {

    @GET("/speakers")
    void getSpeakers(Callback<SpeakerResponseList> speakerResponseListCallback);

    @GET("/sponsors")
    void getSponsors(Callback<SponsorResponseList> speakerResponseListCallback);

    @GET("/sessions")
    void getSessions(Callback<SessionResponseList> sessionResponseListCallback);

    @GET("/events")
    void getEvents(Callback<EventResponseList> eventResponseListCallback);

    @GET("/tracks")
    void getTracks(Callback<TrackResponseList> trackResponseListCallback);

    @GET("/microlocations")
    void getMicrolocations(Callback<MicrolocationResponseList> microlocationResponseListCallback);
}
