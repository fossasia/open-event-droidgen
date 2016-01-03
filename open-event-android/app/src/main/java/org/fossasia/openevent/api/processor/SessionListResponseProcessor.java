package org.fossasia.openevent.api.processor;

import android.util.Log;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.api.protocol.SessionResponseList;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.SessionDownloadEvent;
import org.fossasia.openevent.utils.CommonTaskLoop;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by MananWason on 27-05-2015.
 */
public class SessionListResponseProcessor implements Callback<SessionResponseList> {
    private static final String TAG = "Session";

    @Override
    public void success(final SessionResponseList sessionResponseList, Response response) {
        CommonTaskLoop.getInstance().post(new Runnable() {

            @Override
            public void run() {
                ArrayList<String> queries = new ArrayList<String>();
                for (Session session : sessionResponseList.sessions) {
                    String query = session.generateSql();
                    queries.add(query);
                    Log.d(TAG, query);
                }

                DbSingleton dbSingleton = DbSingleton.getInstance();
                dbSingleton.clearDatabase(DbContract.Sessions.TABLE_NAME);
                dbSingleton.insertQueries(queries);
                OpenEventApp.postEventOnUIThread(new SessionDownloadEvent(true));
            }


        });
    }


    @Override
    public void failure(RetrofitError error) {
        // Do something with failure, raise an event etc.
        OpenEventApp.getEventBus().post(new SessionDownloadEvent(false));
    }

}