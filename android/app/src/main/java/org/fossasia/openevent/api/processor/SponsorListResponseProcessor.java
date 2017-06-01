package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.dbutils.RealmDataRepository;
import org.fossasia.openevent.events.SponsorDownloadEvent;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MananWason on 26-05-2015.
 */
public class SponsorListResponseProcessor implements Callback<List<Sponsor>> {

    @Override
    public void onResponse(Call<List<Sponsor>> call, final Response<List<Sponsor>> response) {
        if (response.isSuccessful()) {
            RealmDataRepository.getDefaultInstance()
                    .saveSponsors(response.body())
                    .subscribe();

            OpenEventApp.postEventOnUIThread(new SponsorDownloadEvent(true));
        } else {
            OpenEventApp.getEventBus().post(new SponsorDownloadEvent(false));
        }

    }

    @Override
    public void onFailure(Call<List<Sponsor>> call, Throwable t) {
        OpenEventApp.getEventBus().post(new SponsorDownloadEvent(false));
    }
}