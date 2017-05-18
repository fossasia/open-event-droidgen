package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.TracksDownloadEvent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


/**
 * Created by MananWason on 27-05-2015.
 */
public class TrackListResponseProcessor implements Callback<List<Track>> {
    //private static final String TAG = "TRACKS";

    @Override
    public void onResponse(Call<List<Track>> call, final Response<List<Track>> response) {
        if (response.isSuccessful()) {
            Completable.fromAction(() -> {
                ArrayList<String> queries = new ArrayList<>();

                for (Track track : response.body()) {
                    String query = track.generateSql();
                    queries.add(query);
                    Timber.d(query);
                }

                DbSingleton dbSingleton = DbSingleton.getInstance();
                dbSingleton.clearTable(DbContract.Tracks.TABLE_NAME);
                dbSingleton.insertQueries(queries);

                OpenEventApp.postEventOnUIThread(new TracksDownloadEvent(true));
            }).subscribeOn(Schedulers.computation()).subscribe();
        } else {
            OpenEventApp.getEventBus().post(new TracksDownloadEvent(false));
        }
    }

    @Override
    public void onFailure(Call<List<Track>> call, Throwable t) {
        OpenEventApp.getEventBus().post(new TracksDownloadEvent(false));
    }
}