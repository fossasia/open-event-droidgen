package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.events.TracksDownloadEvent;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by MananWason on 27-05-2015.
 */
public class TrackListResponseProcessor implements Callback<List<Track>> {

    @Override
    public void onResponse(Call<List<Track>> call, final Response<List<Track>> response) {
        if (response.isSuccessful()) {
            RealmDataRepository.getDefaultInstance()
                    .saveTracks(response.body())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> OpenEventApp.getEventBus().post(new TracksDownloadEvent(true)));
        } else {
            OpenEventApp.getEventBus().post(new TracksDownloadEvent(false));
        }
    }

    @Override
    public void onFailure(Call<List<Track>> call, Throwable t) {
        OpenEventApp.getEventBus().post(new TracksDownloadEvent(false));
    }
}