package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.api.protocol.SessionResponseList;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by MananWason on 27-05-2015.
 */
public class SessionListResponseProcessor implements Callback<SessionResponseList> {

    @Override
    public void success(SessionResponseList sessionResponseList, Response response) {

    }

    @Override
    public void failure(RetrofitError error) {
        // Do something with failure, raise an event etc.
    }
}
