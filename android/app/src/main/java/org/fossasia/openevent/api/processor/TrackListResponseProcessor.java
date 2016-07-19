package org.fossasia.openevent.api.processor;

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
import timber.log.Timber;


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

                    for (Track track : response.body().tracks) {
                        String query = track.generateSql();
                        queries.add(query);
                        Timber.d(query);
                    }

                    DbSingleton dbSingleton = DbSingleton.getInstance();
                    dbSingleton.clearTable(DbContract.Tracks.TABLE_NAME);
                    dbSingleton.insertQueries(queries);

                    OpenEventApp.postEventOnUIThread(new TracksDownloadEvent(true));
                }
            });
        } else {
            OpenEventApp.getEventBus().post(new TracksDownloadEvent(false));
        }
    }

    @Override
    public void onFailure(Call<TrackResponseList> call, Throwable t) {
        OpenEventApp.getEventBus().post(new TracksDownloadEvent(false));
    }
}
