package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.api.protocol.TrackResponseList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by MananWason on 27-05-2015.
 */
public class TrackListResponseProcessor implements Callback<TrackResponseList> {
    @Override
    public void success(TrackResponseList tracksResponseList, Response response) {

    }

    @Override
    public void failure(RetrofitError error) {

    }
}
