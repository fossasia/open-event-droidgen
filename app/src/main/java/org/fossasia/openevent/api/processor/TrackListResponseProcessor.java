package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.api.protocol.TrackResponseList;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.TracksDownloadEvent;
import org.fossasia.openevent.utils.CommonTaskLoop;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by MananWason on 27-05-2015.
 */
public class TrackListResponseProcessor implements Callback<TrackResponseList> {
    private static final String TAG = "Tracks";

    @Override
    public void success(final TrackResponseList tracksResponseList, Response response) {
        CommonTaskLoop.getInstance().post(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> queries = new ArrayList<>();

                Timber.tag(TAG).d("run" + tracksResponseList.tracks.size());
                for (Track track : tracksResponseList.tracks) {
                    String query = track.generateSql();
                    queries.add(query);
                    Timber.tag(TAG).d(query);
                }


                DbSingleton dbSingleton = DbSingleton.getInstance();
                dbSingleton.clearDatabase(DbContract.Tracks.TABLE_NAME);
                dbSingleton.insertQueries(queries);

                //Post success on the bus
                OpenEventApp.postEventOnUIThread(new TracksDownloadEvent(true));
            }
        });


    }

    @Override
    public void failure(RetrofitError error) {

        //Post failure on the bus
        OpenEventApp.getEventBus().post(new TracksDownloadEvent(false));
    }
}
