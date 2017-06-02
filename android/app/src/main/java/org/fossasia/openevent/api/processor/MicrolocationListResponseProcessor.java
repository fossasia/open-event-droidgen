package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.events.MicrolocationDownloadEvent;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by MananWason on 27-05-2015.
 */
public class MicrolocationListResponseProcessor implements Callback<List<Microlocation>> {

    @Override
    public void onResponse(Call<List<Microlocation>> call, final Response<List<Microlocation>> response) {
        if (response.isSuccessful()) {

            RealmDataRepository.getDefaultInstance()
                    .saveLocations(response.body())
                    .subscribe();

            OpenEventApp.postEventOnUIThread(new MicrolocationDownloadEvent(true));
        } else {
            OpenEventApp.getEventBus().post(new MicrolocationDownloadEvent(false));
        }
    }

    @Override
    public void onFailure(Call<List<Microlocation>> call, Throwable t) {
        OpenEventApp.getEventBus().post(new MicrolocationDownloadEvent(false));
    }
}