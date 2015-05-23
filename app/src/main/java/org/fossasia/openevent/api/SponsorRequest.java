package org.fossasia.openevent.api;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Track;
import org.json.JSONArray;
import org.json.JSONException;
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
                try {
                    JSONArray sponsorsArray = response.getJSONArray("sponsors");
                    ArrayList<Sponsor> sponsors = new ArrayList<Sponsor>(sponsorsArray.length());
                    Sponsor s;
                    Gson g = new Gson();

                    for (int i = 0; i < sponsorsArray.length(); i++) {
                        s = g.fromJson(sponsorsArray.getJSONObject(i).toString(), Sponsor.class);
                        sponsors.add(s);
                    }

                    listener.onEventsFetched(sponsors);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
        errorListener);
    }


    public interface OnDataFetchedListener {
        public void onEventsFetched(ArrayList<Sponsor> sponsors);
    }


}
