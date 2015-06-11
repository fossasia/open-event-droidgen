package org.fossasia.openevent.api.processor;

import android.util.Log;

import org.fossasia.openevent.api.protocol.SponsorResponseList;
import org.fossasia.openevent.data.Sponsor;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.Progress;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by MananWason on 26-05-2015.
 */
public class SponsorListResponseProcessor implements Callback<SponsorResponseList> {
    private static final String TAG = "Sponsors";

    @Override
    public void success(SponsorResponseList sponsorResponseList, Response response) {
        ArrayList<String> queries = new ArrayList<String>();

        for (Sponsor sponsor : sponsorResponseList.sponsors) {
            String query = sponsor.generateSql();
            queries.add(query);
            Log.d(TAG, query);
        }


        DbSingleton dbSingleton = DbSingleton.getInstance();
        dbSingleton.clearDatabase(DbContract.Sponsors.TABLE_NAME);
        dbSingleton.insertQueries(queries);
        Progress progress1 = new Progress();
        Log.d(TAG, progress1.progress + " BEF");
        progress1.increaseProgress();
        Log.d(TAG, progress1.progress + " AF");
    }

    @Override
    public void failure(RetrofitError error) {

    }
}
