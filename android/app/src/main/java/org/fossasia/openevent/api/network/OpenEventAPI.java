package org.fossasia.openevent.api.network;

import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.SessionType;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.data.auth.Login;
import org.fossasia.openevent.data.auth.LoginResponse;
import org.fossasia.openevent.data.auth.SignUp;
import org.fossasia.openevent.data.auth.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * User: mohit
 * Date: 25/5/15
 */
public interface OpenEventAPI {

    @POST("../../users")
    Observable<User> signUp(@Body SignUp signUp);

    @POST("../../../auth/session")
    Observable<LoginResponse> login(@Body Login login);

    @GET("speakers?include=sessions&fields[session]=title")
    Call<List<Speaker>> getSpeakers();

    @GET("sponsors")
    Call<List<Sponsor>> getSponsors();

    @GET("sessions?include=microlocation,track&fields[microlocation]=name&fields[track]=name")
    Call<List<Session>> getSessions();

    @GET("../{id}?include=social_links,speakers_call,event_copyright")
    Call<Event> getEvent(@Path("id") int eventId);

    @GET("microlocations")
    Call<List<Microlocation>> getMicrolocations();

    @GET("tracks?include=sessions&fields[session]=title")
    Call<List<Track>> getTracks();

    @GET("session-types")
    Call<List<SessionType>> getSessionTypes();

}