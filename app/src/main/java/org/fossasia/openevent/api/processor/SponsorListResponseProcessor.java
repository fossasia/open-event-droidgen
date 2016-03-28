package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.api.protocol.SponsorResponseList;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.SponsorDownloadEvent;
import org.fossasia.openevent.utils.CommonTaskLoop;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by MananWason on 26-05-2015.
 */
public class SponsorListResponseProcessor implements Callback<SponsorResponseList> {
    private static final String TAG = "Sponsors";

    @Override
    public void success(final SponsorResponseList sponsorResponseList, Response response) {
        CommonTaskLoop.getInstance().post(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> queries = new ArrayList<String>();

                for (Sponsor sponsor : sponsorResponseList.sponsors) {
                    String query = sponsor.generateSql();
                    queries.add(query);
                    Timber.tag(TAG).d(query);
                }


                DbSingleton dbSingleton = DbSingleton.getInstance();
                dbSingleton.clearDatabase(DbContract.Sponsors.TABLE_NAME);
                dbSingleton.insertQueries(queries);
                OpenEventApp.postEventOnUIThread(new SponsorDownloadEvent(true));
            }
        });

    }

    @Override
    public void failure(RetrofitError error) {
        OpenEventApp.getEventBus().post(new SponsorDownloadEvent(false));
    }
}
