package org.fossasia.openevent.api.network;

import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Track;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * User: mohit
 * Date: 25/5/15
 */
public interface OpenEventAPI {

    @GET("speakers")
    Call<List<Speaker>> getSpeakers();

    @GET("sponsors")
    Call<List<Sponsor>> getSponsors();

    @GET("sessions")
    Call<List<Session>> getSessions(@Query("order_by") String orderBy);

    //TODO:Correct event api url to server's
    @GET("event")
    Call<Event> getEvents();

    @GET("microlocations")
    Call<List<Microlocation>> getMicrolocations();

    @GET("tracks")
    Call<List<Track>> getTracks();

}