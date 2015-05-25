package org.fossasia.openevent.api.processor;

import android.util.Log;

import org.fossasia.openevent.api.protocol.SponsorResponseList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by MananWason on 26-05-2015.
 */
public class SponsorListResponseProcessor implements Callback<SponsorResponseList>{

    @Override
    public void success(SponsorResponseList sponsorResponseList, Response response) {

    }

    @Override
    public void failure(RetrofitError error) {

    }
}
