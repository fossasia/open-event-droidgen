package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.api.protocol.EventDatesResponseList;
import org.fossasia.openevent.data.EventDates;
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
 * Created by Manan Wason on 18/06/16.
 */
public class EventDatesListResponseProcessor implements Callback<EventDatesResponseList> {

    ArrayList<String> queries = new ArrayList<>();

    @Override
    public void onResponse(Call<EventDatesResponseList> call, final Response<EventDatesResponseList> response) {
        if (response.isSuccessful()) {
            CommonTaskLoop.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    for (EventDates eventDates : response.body().event) {
                        String query = eventDates.generateSql();
                        queries.add(query);
                        Timber.d(query);
                    }
                    DbSingleton dbSingleton = DbSingleton.getInstance();

                    dbSingleton.clearDatabase(DbContract.Microlocation.TABLE_NAME);
                    dbSingleton.insertQueries(queries);

                    OpenEventApp.postEventOnUIThread(new EventDownloadEvent(true));
                }
            });
        } else {
            OpenEventApp.getEventBus().post(new EventDownloadEvent(false));
        }

    }

    @Override
    public void onFailure(Call<EventDatesResponseList> call, Throwable t) {

    }
}
