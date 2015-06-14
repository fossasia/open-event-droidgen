package org.fossasia.openevent;

import android.test.AndroidTestCase;

import org.fossasia.openevent.api.APIClient;
import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.api.protocol.EventResponseList;
import org.fossasia.openevent.api.protocol.MicrolocationResponseList;
import org.fossasia.openevent.api.protocol.SessionResponseList;
import org.fossasia.openevent.api.protocol.SpeakerResponseList;
import org.fossasia.openevent.api.protocol.SponsorResponseList;
import org.fossasia.openevent.api.protocol.TrackResponseList;

import java.util.concurrent.CountDownLatch;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * User: mohit
 * Date: 25/5/15
 */
public class APITest extends AndroidTestCase {
    public void testSpeakerAPIResponse() throws Exception {
        APIClient client = new APIClient();
        final CountDownLatch latch = new CountDownLatch(1);
        client.getOpenEventAPI().getSpeakers(Urls.EVENT_ID, new Callback<SpeakerResponseList>() {
            @Override
            public void success(SpeakerResponseList speakerResponseList, Response response) {
                assertNotNull(speakerResponseList.speakers);
                // Assert that the list size > 0
                assertTrue(speakerResponseList.speakers.size() > 0);
                latch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail("API Request Failed");
                latch.countDown();
            }
        });
        latch.await();
    }

    public void testSponsorAPIResponse() throws Exception {
        APIClient client = new APIClient();
        final CountDownLatch latch = new CountDownLatch(1);
        client.getOpenEventAPI().getSponsors(Urls.EVENT_ID, new Callback<SponsorResponseList>() {
            @Override
            public void success(SponsorResponseList sponsorResponseList, Response response) {
                assertNotNull(sponsorResponseList.sponsors);
                // Assert that the list size > 0
                assertTrue(sponsorResponseList.sponsors.size() > 0);
                latch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail("API Request Failed");
                latch.countDown();
            }
        });
        latch.await();
    }

    public void testEventAPIResponse() throws Exception {
        APIClient client = new APIClient();
        final CountDownLatch latch = new CountDownLatch(1);
        client.getOpenEventAPI().getEvents(Urls.EVENT_ID, new Callback<EventResponseList>() {
            @Override
            public void success(EventResponseList eventResponseList, Response response) {
                assertNotNull(eventResponseList.event);
                assertTrue(eventResponseList.event.size() > 0);
                latch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail("API Request Failed");
                latch.countDown();
            }
        });
        latch.await();
    }

    public void testSessionAPIResponse() throws Exception {
        APIClient client = new APIClient();
        final CountDownLatch latch = new CountDownLatch(1);
        client.getOpenEventAPI().getSessions(Urls.EVENT_ID, new Callback<SessionResponseList>() {
            @Override
            public void success(SessionResponseList sessionResponseList, Response response) {
                assertNotNull(sessionResponseList.sessions);
                assertTrue(sessionResponseList.sessions.size() > 0);
                latch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail("API Request Failed");
                latch.countDown();
            }
        });
        latch.await();
    }

    public void testTrackAPIResponse() throws Exception {
        APIClient client = new APIClient();
        final CountDownLatch latch = new CountDownLatch(1);
        client.getOpenEventAPI().getTracks(Urls.EVENT_ID, new Callback<TrackResponseList>() {
            @Override
            public void success(TrackResponseList trackResponseList, Response response) {
                assertNotNull(trackResponseList.tracks);
                assertTrue(trackResponseList.tracks.size() > 0);
                latch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail("API Request Failed");
                latch.countDown();
            }
        });
        latch.await();
    }

    public void testMicrolocationAPIResponse() throws Exception {
        APIClient client = new APIClient();
        final CountDownLatch latch = new CountDownLatch(1);
        client.getOpenEventAPI().getMicrolocations(Urls.EVENT_ID, new Callback<MicrolocationResponseList>() {
            @Override
            public void success(MicrolocationResponseList microlocationResponseList, Response response) {
                assertNotNull(microlocationResponseList.microlocations);
                assertTrue(microlocationResponseList.microlocations.size() > 0);
                latch.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail("API Request Failed");
                latch.countDown();
            }
        });
        latch.await();
    }
}
