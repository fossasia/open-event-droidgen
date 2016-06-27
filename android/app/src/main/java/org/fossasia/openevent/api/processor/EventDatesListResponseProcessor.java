package org.fossasia.openevent.api.processor;

import android.util.Log;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.api.protocol.EventDatesResponseList;
import org.fossasia.openevent.data.EventDates;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.MicrolocationDownloadEvent;
import org.fossasia.openevent.utils.CommonTaskLoop;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Manan Wason on 18/06/16.
 */
public class EventDatesListResponseProcessor implements Callback<EventDatesResponseList> {
    private static final String TAG = "EventDatesListProcessor";

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
                        Log.d(TAG, query);
                    }
                    DbSingleton dbSingleton = DbSingleton.getInstance();

                    dbSingleton.clearDatabase(DbContract.Microlocation.TABLE_NAME);
                    dbSingleton.insertQueries(queries);

                    OpenEventApp.postEventOnUIThread(new MicrolocationDownloadEvent(true));
                }
            });
        } else {
            OpenEventApp.getEventBus().post(new MicrolocationDownloadEvent(false));
        }

    }

    @Override
    public void onFailure(Call<EventDatesResponseList> call, Throwable t) {

    }
}
