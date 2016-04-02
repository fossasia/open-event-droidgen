package org.fossasia.openevent.api.processor;

import android.util.Log;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.api.protocol.TrackResponseList;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.TracksDownloadEvent;
import org.fossasia.openevent.utils.CommonTaskLoop;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by MananWason on 27-05-2015.
 */
public class TrackListResponseProcessor implements Callback<TrackResponseList> {
    private static final String TAG = "Tracks";

    @Override
    public void onResponse(Call<TrackResponseList> call, final Response<TrackResponseList> response) {
        if (response.isSuccessful()) {
            CommonTaskLoop.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    ArrayList<String> queries = new ArrayList<>();

                    Log.d(TAG, "run" + response.body().tracks.size());
                    for (Track track : response.body().tracks) {
                        String query = track.generateSql();
                        queries.add(query);
                        Log.d(TAG, query);
                    }


                    DbSingleton dbSingleton = DbSingleton.getInstance();
                    dbSingleton.clearDatabase(DbContract.Tracks.TABLE_NAME);
                    dbSingleton.insertQueries(queries);

                    //Post success on the bus
                    OpenEventApp.postEventOnUIThread(new TracksDownloadEvent(true));
                }
            });
        } else {
            //Post failure on the bus
            OpenEventApp.getEventBus().post(new TracksDownloadEvent(false));
        }
    }

    @Override
    public void onFailure(Call<TrackResponseList> call, Throwable t) {
        //Post failure on the bus
        OpenEventApp.getEventBus().post(new TracksDownloadEvent(false));
    }
}
