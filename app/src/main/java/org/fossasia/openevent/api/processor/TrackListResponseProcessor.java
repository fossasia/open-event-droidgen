package org.fossasia.openevent.api.processor;

import android.util.Log;

import com.squareup.otto.Bus;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.api.protocol.TrackResponseList;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.FailedDownload;
import org.fossasia.openevent.events.TracksDownloadEvent;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by MananWason on 27-05-2015.
 */
public class TrackListResponseProcessor implements Callback<TrackResponseList> {
    private static final String TAG = "Tracks";

    @Override
    public void success(TrackResponseList tracksResponseList, Response response) {
        ArrayList<String> queries = new ArrayList<String>();

        for (Track track : tracksResponseList.tracks) {
            String query = track.generateSql();
            queries.add(query);
            Log.d(TAG, query);
        }


        DbSingleton dbSingleton = DbSingleton.getInstance();
        dbSingleton.clearDatabase(DbContract.Tracks.TABLE_NAME);
        dbSingleton.insertQueries(queries);

        Bus bus = OpenEventApp.getEventBus();
        bus.post(new TracksDownloadEvent());
    }

    @Override
    public void failure(RetrofitError error) {
        Bus bus = OpenEventApp.getEventBus();
        bus.post(new FailedDownload());
    }
}
