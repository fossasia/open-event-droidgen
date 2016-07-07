package org.fossasia.openevent;

import android.test.AndroidTestCase;

import org.fossasia.openevent.api.APIClient;
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.api.protocol.MicrolocationResponseList;
import org.fossasia.openevent.api.protocol.SessionResponseList;
import org.fossasia.openevent.api.protocol.SponsorResponseList;
import org.fossasia.openevent.api.protocol.TrackResponseList;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Speaker;

import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * User: mohit
 * Date: 25/5/15
 */
public class APITest extends AndroidTestCase {
    public void testSpeakerAPIResponse() throws Exception {
        APIClient client = new APIClient();
        final CountDownLatch latch = new CountDownLatch(1);
        client.getOpenEventAPI().getSpeakers().enqueue(new Callback<Speaker[]>() {
            @Override
            public void onResponse(Call<Speaker[]> call, Response<Speaker[]> response) {
                if (response.isSuccessful()) {
                    assertNotNull(response.body());
                    // Assert that the list size > 0
                    assertTrue(response.body().length > 0);
                    latch.countDown();
                } else {
                    fail("API Request Failed");
                    latch.countDown();
                }
            }

            @Override
            public void onFailure(Call<Speaker[]> call, Throwable t) {
                fail("API Request Failed");
                latch.countDown();
            }
        });

        latch.await();
    }

    public void testSponsorAPIResponse() throws Exception {
        APIClient client = new APIClient();
        final CountDownLatch latch = new CountDownLatch(1);
        client.getOpenEventAPI().getSponsors(Urls.EVENT_ID).enqueue(new Callback<SponsorResponseList>() {
            @Override
            public void onResponse(Call<SponsorResponseList> call, Response<SponsorResponseList> response) {
                if (response.isSuccessful()) {
                    assertNotNull(response.body().sponsors);
                    // Assert that the list size > 0
                    assertTrue(response.body().sponsors.size() > 0);
                    latch.countDown();

                } else {
                    fail("API Request Failed");
                    latch.countDown();
                }
            }

            @Override
            public void onFailure(Call<SponsorResponseList> call, Throwable t) {
                fail("API Request Failed");
                latch.countDown();
            }
        });
        latch.await();
    }

    public void testEventAPIResponse() throws Exception {
        APIClient client = new APIClient();
        final CountDownLatch latch = new CountDownLatch(1);
        client.getOpenEventAPI().getEvents().enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful()) {
                    assertNotNull(response.body());
                    assertTrue(response.body()!=null);
                    latch.countDown();
                } else {
                    fail("API Request Failed");
                    latch.countDown();
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                fail("API Request Failed");
                latch.countDown();
            }
        });
        latch.await();
    }

    public void testSessionAPIResponse() throws Exception {
        APIClient client = new APIClient();
        final CountDownLatch latch = new CountDownLatch(1);
        client.getOpenEventAPI().getSessions(Urls.EVENT_ID).enqueue(new Callback<SessionResponseList>() {
            @Override
            public void onResponse(Call<SessionResponseList> call, Response<SessionResponseList> response) {
                if (response.isSuccessful()) {
                    assertNotNull(response.body().sessions);
                    assertTrue(response.body().sessions.size() > 0);
                    latch.countDown();
                } else {
                    fail("API Request Failed");
                    latch.countDown();
                }
            }

            @Override
            public void onFailure(Call<SessionResponseList> call, Throwable t) {
                fail("API Request Failed");
                latch.countDown();
            }
        });
        latch.await();
    }

    public void testTrackAPIResponse() throws Exception {
        APIClient client = new APIClient();
        final CountDownLatch latch = new CountDownLatch(1);
        client.getOpenEventAPI().getTracks(Urls.EVENT_ID).enqueue(new Callback<TrackResponseList>() {
            @Override
            public void onResponse(Call<TrackResponseList> call, Response<TrackResponseList> response) {
                if (response.isSuccessful()) {
                    assertNotNull(response.body().tracks);
                    assertTrue(response.body().tracks.size() > 0);
                    latch.countDown();
                } else {
                    fail("API Request Failed");
                    latch.countDown();
                }
            }

            @Override
            public void onFailure(Call<TrackResponseList> call, Throwable t) {
                fail("API Request Failed");
                latch.countDown();
            }
        });
        latch.await();
    }

    public void testMicrolocationAPIResponse() throws Exception {
        APIClient client = new APIClient();
        final CountDownLatch latch = new CountDownLatch(1);
        client.getOpenEventAPI().getMicrolocations(Urls.EVENT_ID).enqueue(new Callback<MicrolocationResponseList>() {
            @Override
            public void onResponse(Call<MicrolocationResponseList> call, Response<MicrolocationResponseList> response) {
                if (response.isSuccessful()) {
                    assertNotNull(response.body().microlocations);
                    assertTrue(response.body().microlocations.size() > 0);
                    latch.countDown();
                } else {
                    fail("API Request Failed");
                    latch.countDown();
                }
            }

            @Override
            public void onFailure(Call<MicrolocationResponseList> call, Throwable t) {
                fail("API Request Failed");
                latch.countDown();
            }
        });
        latch.await();
    }
}
