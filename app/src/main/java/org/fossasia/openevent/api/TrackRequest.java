package org.fossasia.openevent.api;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.Track;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by championswimmer on 23/5/15.
 */
public class TrackRequest extends JsonObjectRequest {

    public TrackRequest(final OnDataFetchedListener listener, Response.ErrorListener errorListener) {
        super(Method.GET, Urls.Get.TRACKS, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: Turn json response into arraylist of Events
                listener.onEventsFetched(new ArrayList<Track>());
            }
        },
        errorListener);
    }


    public interface OnDataFetchedListener {
        public void onEventsFetched(ArrayList<Track> tracks);
    }


}
