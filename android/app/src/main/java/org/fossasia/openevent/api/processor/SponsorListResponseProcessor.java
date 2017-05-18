package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.SponsorDownloadEvent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by MananWason on 26-05-2015.
 */
public class SponsorListResponseProcessor implements Callback<List<Sponsor>> {

    @Override
    public void onResponse(Call<List<Sponsor>> call, final Response<List<Sponsor>> response) {
        if (response.isSuccessful()) {
            Completable.fromAction(() -> {
                ArrayList<String> queries = new ArrayList<>();

                for (Sponsor sponsor : response.body()) {
                    sponsor.changeSponsorTypeToInt(sponsor.getType());
                    String query = sponsor.generateSql();
                    queries.add(query);
                    Timber.d(query);
                }


                DbSingleton dbSingleton = DbSingleton.getInstance();
                dbSingleton.clearTable(DbContract.Sponsors.TABLE_NAME);
                dbSingleton.insertQueries(queries);
                OpenEventApp.postEventOnUIThread(new SponsorDownloadEvent(true));
            }).subscribeOn(Schedulers.computation()).subscribe();
        } else {
            OpenEventApp.getEventBus().post(new SponsorDownloadEvent(false));
        }

    }

    @Override
    public void onFailure(Call<List<Sponsor>> call, Throwable t) {
        OpenEventApp.getEventBus().post(new SponsorDownloadEvent(false));
    }
}