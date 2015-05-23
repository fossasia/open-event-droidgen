package org.fossasia.openevent.api;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.data.Sponsor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by championswimmer on 23/5/15.
 */
public class SpeakerRequest extends JsonObjectRequest {

    public SpeakerRequest(final OnDataFetchedListener listener, Response.ErrorListener errorListener) {
        super(Method.GET, Urls.Get.SPEAKERS, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray speakersArray = response.getJSONArray("speakers");
                    ArrayList<Speaker> speakers = new ArrayList<Speaker>(speakersArray.length());
                    Speaker s;
                    Gson g = new Gson();

                    for (int i = 0; i < speakersArray.length(); i++) {
                        s = g.fromJson(speakersArray.getJSONObject(i).toString(), Speaker.class);
                        speakers.add(s);
                    }

                    listener.onEventsFetched(speakers);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
        errorListener);
    }

    public interface OnDataFetchedListener {
        public void onEventsFetched(ArrayList<Speaker> speakers);
    }


}
