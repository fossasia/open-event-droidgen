package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.api.protocol.SponsorResponseList;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.SponsorDownloadEvent;
import org.fossasia.openevent.utils.CommonTaskLoop;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by MananWason on 26-05-2015.
 */
public class SponsorListResponseProcessor implements Callback<SponsorResponseList> {

    @Override
    public void onResponse(Call<SponsorResponseList> call, final Response<SponsorResponseList> response) {
        if (response.isSuccessful()) {
            CommonTaskLoop.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    ArrayList<String> queries = new ArrayList<>();

                    for (Sponsor sponsor : response.body().sponsors) {
                        String query = sponsor.generateSql();
                        queries.add(query);
                        Timber.d(query);
                    }


                    DbSingleton dbSingleton = DbSingleton.getInstance();
                    dbSingleton.clearTable(DbContract.Sponsors.TABLE_NAME);
                    dbSingleton.insertQueries(queries);
                    OpenEventApp.postEventOnUIThread(new SponsorDownloadEvent(true));
                }
            });
        } else {
            OpenEventApp.getEventBus().post(new SponsorDownloadEvent(false));
        }

    }

    @Override
    public void onFailure(Call<SponsorResponseList> call, Throwable t) {
        OpenEventApp.getEventBus().post(new SponsorDownloadEvent(false));
    }
}
