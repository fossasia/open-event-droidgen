package org.fossasia.openevent;

import android.test.AndroidTestCase;

import org.fossasia.openevent.api.APIClient;
import org.fossasia.openevent.api.protocol.SpeakerResponseList;

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
        client.getOpenEventAPI().getSpeakers(new Callback<SpeakerResponseList>() {
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
}
