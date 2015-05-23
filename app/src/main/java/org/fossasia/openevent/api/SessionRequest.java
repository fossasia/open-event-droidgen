package org.fossasia.openevent.api;

import android.app.DownloadManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.fossasia.openevent.data.Session;
import org.json.JSONArray;
import org.json.JSONException;
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
                try {
                    JSONArray sessionsArray = response.getJSONArray("sessions");
                    ArrayList<Session> sessions = new ArrayList<Session>(sessionsArray.length());
                    Session s;
                    Gson g = new Gson();

                    for (int i = 0; i < sessionsArray.length(); i++) {
                        s = g.fromJson(sessionsArray.getJSONObject(i).toString(), Session.class);
                        sessions.add(s);
                    }

                    listener.onEventsFetched(sessions);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
        errorListener);
    }

    public interface OnDataFetchedListener {
        public void onEventsFetched (ArrayList<Session> sessions);
    }


}
