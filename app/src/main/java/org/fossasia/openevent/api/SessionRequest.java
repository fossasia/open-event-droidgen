package org.fossasia.openevent.api;

import android.app.DownloadManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.fossasia.openevent.data.Session;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by championswimmer on 23/5/15.
 */
public class SessionRequest extends JsonObjectRequest {

    public SessionRequest(final OnDataFetchedListener listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, Urls.Get.SESSIONS, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //TODO: Turn json response into arraylist of Events
                listener.onEventsFetched(new ArrayList<Session>());
            }
        },
        errorListener);
    }

    public interface OnDataFetchedListener {
        public void onEventsFetched (ArrayList<Session> sessions);
    }


}
