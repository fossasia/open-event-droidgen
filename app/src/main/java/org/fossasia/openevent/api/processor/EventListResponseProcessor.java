package org.fossasia.openevent.api.processor;

import android.util.Log;

import org.fossasia.openevent.api.protocol.EventResponseList;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.Progress;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by MananWason on 27-05-2015.
 */
public class EventListResponseProcessor implements Callback<EventResponseList> {
    private static final String TAG = "Events";

    @Override
    public void success(EventResponseList eventResponseList, Response response) {
        ArrayList<String> queries = new ArrayList<String>();

        for (Event event : eventResponseList.event) {
            String query = event.generateSql();
            queries.add(query);
            Log.d(TAG, query);
        }

        DbSingleton dbSingleton = DbSingleton.getInstance();
        dbSingleton.clearDatabase(DbContract.Event.TABLE_NAME);
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
