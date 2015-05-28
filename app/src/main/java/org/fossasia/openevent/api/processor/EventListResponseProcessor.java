package org.fossasia.openevent.api.processor;

import android.util.Log;

import org.fossasia.openevent.api.protocol.EventResponseList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by MananWason on 27-05-2015.
 */
public class EventListResponseProcessor implements Callback<EventResponseList> {
    private static final String TAG = "Events";
    @Override
    public void success(EventResponseList eventResponseList, Response response) {
    }

    @Override
    public void failure(RetrofitError error) {

    }
}
