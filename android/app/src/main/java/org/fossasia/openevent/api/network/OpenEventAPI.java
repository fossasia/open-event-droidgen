package org.fossasia.openevent.api.network;

import org.fossasia.openevent.api.protocol.EventDatesResponseList;
import org.fossasia.openevent.api.protocol.VersionResponseList;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Track;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * User: mohit
 * Date: 25/5/15
 */
public interface OpenEventAPI {

    @GET("event/{id}/speakers.json")
    Call<List<Speaker>> getSpeakers(@Path("id") int id);

    @GET("event/{id}/sponsors.json")
    Call<List<Sponsor>> getSponsors(@Path("id") int id);

    @GET("event/{id}/sessions.json")
    Call<List<Session>> getSessions(@Path("id") int id);

    //TODO:Correct event api url to server's
    @GET("event/1/event.json")
    Call<Event> getEvents();

    @GET("event/{id}/microlocations.json")
    Call<List<Microlocation>> getMicrolocations(@Path("id") int id);

    @GET("event/{id}/tracks.json")
    Call<List<Track>> getTracks(@Path("id") int id);

    //https://raw.githubusercontent.com/fossasia/open-event/master/testapi/event/1/version
    @GET("event/{id}/version.json")
    Call<VersionResponseList> getVersion(@Path("id") int id);

    @GET("event/{id}/eventDates.json")
    Call<EventDatesResponseList> getDates(@Path("id") int id);

}
