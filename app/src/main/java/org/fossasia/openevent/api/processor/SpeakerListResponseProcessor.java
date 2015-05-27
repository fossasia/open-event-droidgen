package org.fossasia.openevent.api.processor;

import android.util.Log;

import org.fossasia.openevent.api.protocol.SpeakerResponseList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * User: mohit
 * Date: 25/5/15
 */
public class SpeakerListResponseProcessor implements Callback<SpeakerResponseList> {
    @Override
    public void success(SpeakerResponseList speakerResponseList, Response response) {
        // Do something with successful response
        // Log.d("SPEAKERS ", speakerResponseList.toString());

    }

    @Override
    public void failure(RetrofitError error) {
        // Do something with failure, raise an event etc.
    }
}
