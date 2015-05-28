package org.fossasia.openevent.api.processor;

import android.util.Log;

import org.fossasia.openevent.api.protocol.MicrolocationResponseList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by MananWason on 27-05-2015.
 */
public class MicrolocationListResponseProcessor implements Callback<MicrolocationResponseList> {
    private static final String TAG = "Microprocessor";

    @Override
    public void success(MicrolocationResponseList microlocationResponseList, Response response) {


    }
    @Override
    public void failure(RetrofitError error) {

    }
}
