package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.api.protocol.EventResponseList;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.EventDownloadEvent;
import org.fossasia.openevent.utils.CommonTaskLoop;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 27-05-2015
 */
public class EventListResponseProcessor implements Callback<EventResponseList> {
    private static final String TAG = "Events";

    @Override
    public void success(final EventResponseList eventResponseList, Response response) {
        CommonTaskLoop.getInstance().post(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> queries = new ArrayList<String>();

                for (Event event : eventResponseList.event) {
                    String query = event.generateSql();
                    queries.add(query);
                    Timber.tag(TAG).d(query);
                }

                DbSingleton dbSingleton = DbSingleton.getInstance();
                dbSingleton.clearDatabase(DbContract.Event.TABLE_NAME);
                dbSingleton.insertQueries(queries);

                OpenEventApp.postEventOnUIThread(new EventDownloadEvent(true));

            }
        });

    }


    @Override
    public void failure(RetrofitError error) {
        OpenEventApp.getEventBus().post(new EventDownloadEvent(false));
    }
}
