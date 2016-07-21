package org.fossasia.openevent.api.network;

import org.fossasia.openevent.api.protocol.EventDatesResponseList;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Track;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * User: mohit
 * Date: 25/5/15
 */
public interface OpenEventAPI {

    @GET("speakers.json")
    Call<List<Speaker>> getSpeakers();

    @GET("sponsors.json")
    Call<List<Sponsor>> getSponsors();

    @GET("sessions.json")
    Call<List<Session>> getSessions();

    //TODO:Correct event api url to server's
    @GET("event.json")
    Call<Event> getEvents();

    @GET("microlocations.json")
    Call<List<Microlocation>> getMicrolocations();

    @GET("tracks.json")
    Call<List<Track>> getTracks();

    //https://raw.githubusercontent.com/fossasia/open-event/master/testapi/event/1/version
//    @GET("version.json")
//    Call<VersionResponseList> getVersion();

    @GET("eventDates.json")
    Call<EventDatesResponseList> getDates();

}