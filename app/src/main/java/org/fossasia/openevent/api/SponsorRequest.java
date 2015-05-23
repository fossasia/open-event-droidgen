package org.fossasia.openevent.api;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Track;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by championswimmer on 23/5/15.
 */
public class SponsorRequest extends JsonObjectRequest {

    public SponsorRequest(final OnDataFetchedListener listener, Response.ErrorListener errorListener) {
        super(Method.GET, Urls.Get.SPONSORS, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: Turn json response into arraylist of Events
                listener.onEventsFetched(new ArrayList<Sponsor>());
            }
        },
        errorListener);
    }


    public interface OnDataFetchedListener {
        public void onEventsFetched(ArrayList<Sponsor> sponsors);
    }


}
