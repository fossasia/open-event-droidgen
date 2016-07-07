package org.fossasia.openevent.api.processor;

import android.util.Log;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.SponsorDownloadEvent;
import org.fossasia.openevent.utils.CommonTaskLoop;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MananWason on 26-05-2015.
 */
public class SponsorListResponseProcessor implements Callback<List<Sponsor>> {
    private static final String TAG = "Sponsors";

    @Override
    public void onResponse(Call<List<Sponsor>> call, final Response<List<Sponsor>> response) {
        if (response.isSuccessful()) {
            CommonTaskLoop.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    ArrayList<String> queries = new ArrayList<>();

                    for (Sponsor sponsor : response.body()) {
                        String query = sponsor.generateSql();
                        queries.add(query);
                        Log.d(TAG, query);
                    }


                    DbSingleton dbSingleton = DbSingleton.getInstance();
                    dbSingleton.clearDatabase(DbContract.Sponsors.TABLE_NAME);
                    dbSingleton.insertQueries(queries);
                    OpenEventApp.postEventOnUIThread(new SponsorDownloadEvent(true));
                }
            });
        } else {
            OpenEventApp.getEventBus().post(new SponsorDownloadEvent(false));
        }

    }

    @Override
    public void onFailure(Call<List<Sponsor>> call, Throwable t) {
        OpenEventApp.getEventBus().post(new SponsorDownloadEvent(false));
    }
}
