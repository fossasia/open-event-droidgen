package org.fossasia.openevent.common.api;

import org.fossasia.openevent.core.auth.model.ImageResponse;
import org.fossasia.openevent.core.auth.model.Login;
import org.fossasia.openevent.core.auth.model.LoginResponse;
import org.fossasia.openevent.core.auth.model.UploadImage;
import org.fossasia.openevent.core.auth.model.User;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.FAQ;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Notification;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.SessionType;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Track;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OpenEventAPI {

    @POST("../../users")
    Observable<User> signUp(@Body User user);

    @POST("../../../auth/session")
    Observable<LoginResponse> login(@Body Login login);

    @GET("../../users/{id}")
    Observable<User> getUser(@Path("id") long id);

    @PATCH("../../users/{id}")
    Observable<User> updateUser(@Body User user, @Path("id") long id);

    @POST("../../upload/image")
    Observable<ImageResponse> uploadImage(@Body UploadImage uploadImage);

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

    @GET("faqs")
    Observable<List<FAQ>> getFAQs();

    @GET("../../users/{id}/notifications")
    Observable<List<Notification>> getNotifications(@Path("id") long id);

}