package org.fossasia.openevent.api.processor;

import android.util.Log;

import org.fossasia.openevent.api.protocol.EventResponseList;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;

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


        String query = eventResponseList.event.generateSql();
        queries.add(query);
        Log.d(TAG, query);


        DbSingleton dbSingleton = DbSingleton.getInstance();
        dbSingleton.clearDatabase(DbContract.Event.TABLE_NAME);
        dbSingleton.insertQueries(queries);

    }


    @Override
    public void failure(RetrofitError error) {

    }
}
