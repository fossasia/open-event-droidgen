package org.fossasia.openevent;

import android.test.AndroidTestCase;

import org.fossasia.openevent.api.APIClient;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Track;

import java.util.List;
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
        final CountDownLatch latch = new CountDownLatch(1);
        APIClient.getOpenEventAPI().getSpeakers().enqueue(new Callback<List<Speaker>>() {
            @Override
            public void onResponse(Call<List<Speaker>> call, Response<List<Speaker>> response) {
                if (response.isSuccessful()) {
                    assertNotNull(response.body());
                    // Assert that the list size > 0
                    assertTrue(response.body().size() > 0);
                    latch.countDown();
                } else {
                    fail("API Request Failed");
                    latch.countDown();
                }
            }

            @Override
            public void onFailure(Call<List<Speaker>> call, Throwable t) {
                fail("API Request Failed");
                latch.countDown();
            }
        });

        latch.await();
    }

    public void testSponsorAPIResponse() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        APIClient.getOpenEventAPI().getSponsors().enqueue(new Callback<List<Sponsor>>() {
            @Override
            public void onResponse(Call<List<Sponsor>> call, Response<List<Sponsor>> response) {
                if (response.isSuccessful()) {
                    assertNotNull(response.body());
                    // Assert that the list size > 0
                    assertTrue(response.body().size() > 0);
                    latch.countDown();

                } else {
                    fail("API Request Failed");
                    latch.countDown();
                }
            }

            @Override
            public void onFailure(Call<List<Sponsor>> call, Throwable t) {
                fail("API Request Failed");
                latch.countDown();
            }
        });
        latch.await();
    }

    public void testEventAPIResponse() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        APIClient.getOpenEventAPI().getEvent(59).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful()) {
                    assertNotNull(response.body());
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
        final CountDownLatch latch = new CountDownLatch(1);
        APIClient.getOpenEventAPI().getSessions().enqueue(new Callback<List<Session>>() {
            @Override
            public void onResponse(Call<List<Session>> call, Response<List<Session>> response) {
                if (response.isSuccessful()) {
                    assertNotNull(response.body());
                    assertTrue(response.body().size() > 0);
                    latch.countDown();
                } else {
                    fail("API Request Failed");
                    latch.countDown();
                }
            }

            @Override
            public void onFailure(Call<List<Session>> call, Throwable t) {
                fail("API Request Failed");
                latch.countDown();
            }
        });
        latch.await();
    }

    public void testTrackAPIResponse() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        APIClient.getOpenEventAPI().getTracks().enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                if (response.isSuccessful()) {
                    assertNotNull(response.body());
                    assertTrue(response.body().size() > 0);
                    latch.countDown();
                } else {
                    fail("API Request Failed");
                    latch.countDown();
                }
            }

            @Override
            public void onFailure(Call<List<Track>> call, Throwable t) {
                fail("API Request Failed");
                latch.countDown();
            }
        });
        latch.await();
    }

    public void testMicrolocationAPIResponse() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        APIClient.getOpenEventAPI().getMicrolocations().enqueue(new Callback<List<Microlocation>>() {
            @Override
            public void onResponse(Call<List<Microlocation>> call, Response<List<Microlocation>> response) {
                if (response.isSuccessful()) {
                    assertNotNull(response.body());
                    assertTrue(response.body().size() > 0);
                    latch.countDown();
                } else {
                    fail("API Request Failed");
                    latch.countDown();
                }
            }

            @Override
            public void onFailure(Call<List<Microlocation>> call, Throwable t) {
                fail("API Request Failed");
                latch.countDown();
            }
        });
        latch.await();
    }
}