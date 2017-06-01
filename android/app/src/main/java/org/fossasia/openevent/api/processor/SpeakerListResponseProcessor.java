package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.events.SpeakerDownloadEvent;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * User: mohit
 * Date: 25/5/15
 */
public class SpeakerListResponseProcessor implements Callback<List<Speaker>> {

    @Override
    public void onResponse(Call<List<Speaker>> call, final Response<List<Speaker>> response) {
        final List<Speaker> speakers = response.body();

        if (response.isSuccessful()) {
            RealmDataRepository.getDefaultInstance()
                    .saveSpeakers(speakers)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> OpenEventApp.postEventOnUIThread(new SpeakerDownloadEvent(true)));
        } else {
            OpenEventApp.getEventBus().post(new SpeakerDownloadEvent(false));
        }
    }

    @Override
    public void onFailure(Call<List<Speaker>> call, Throwable t) {
        OpenEventApp.getEventBus().post(new SpeakerDownloadEvent(false));
    }
}