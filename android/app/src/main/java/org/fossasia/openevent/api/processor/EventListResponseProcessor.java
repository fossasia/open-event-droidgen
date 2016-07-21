package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.EventDownloadEvent;
import org.fossasia.openevent.utils.CommonTaskLoop;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * User: MananWason
 * Date: 27-05-2015
 */
public class EventListResponseProcessor implements Callback<Event> {
    private static final String TAG = "Events";

    @Override
    public void onResponse(Call<Event> call, final Response<Event> response) {
        if (response.isSuccessful()) {
            CommonTaskLoop.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    ArrayList<String> queries = new ArrayList<>();

                    Event event = response.body();
                    String query = event.generateSql();
                    queries.add(query);
                    Timber.d(query);
                    DbSingleton dbSingleton = DbSingleton.getInstance();
                    dbSingleton.clearTable(DbContract.Event.TABLE_NAME);
                    dbSingleton.insertQueries(queries);

                    OpenEventApp.postEventOnUIThread(new EventDownloadEvent(true));

                }
            });
        } else {
            OpenEventApp.getEventBus().post(new EventDownloadEvent(false));
        }

    }

    @Override
    public void onFailure(Call<Event> call, Throwable t) {
        OpenEventApp.getEventBus().post(new EventDownloadEvent(false));
    }
}