package org.fossasia.openevent.api;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DbContract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by championswimmer on 23/5/15.
 */
public class SponsorRequest extends Request {
    private static final String TAG = "SponsorRequest";
    private Response.Listener listener;

    public SponsorRequest( String url, Class lass,Response.Listener responseListener, Response.ErrorListener errorListener) {
        super(requestMethod, url, errorListener);
        this.listener = responseListener;
    }


    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
            try {
                    JSONArray sponsorsArray = response.getJSONArray("sponsors");
                    ArrayList<Sponsor> sponsors = new ArrayList<Sponsor>(sponsorsArray.length());
                    Sponsor s;
                    Gson g = new Gson();

                    for (int i = 0; i < sponsorsArray.length(); i++) {
                        s = g.fromJson(sponsorsArray.getJSONObject(i).toString(), Sponsor.class);
                        Log.d(TAG, " " + s);
                        //sponsors.add(s);
                        generateQuery(s);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


        return null;
    }

    @Override
    protected void deliverResponse(Object response) {

    }

    @Override
    public int compareTo(Object another) {
        return 0;
    }


    public interface OnDataFetchedListener {
        public void onEventsFetched(ArrayList<Sponsor> sponsors);
    }

    public String generateQuery(Sponsor sponsor) {
        String query = "INSERT INTO %s VALUES ('%d', '%s', '%s', '%s')";
        query = String.format(query, DbContract.Sponsors.TABLE_NAME, sponsor.getId(), sponsor.getLogo(), sponsor.getName(), sponsor.getUrl());
        return query;
    }


}
