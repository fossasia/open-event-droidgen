package org.fossasia.openevent.api.processor;

import android.util.Log;

import com.squareup.otto.Bus;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.api.protocol.MicrolocationResponseList;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.MicrolocationDownloadEvent;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by MananWason on 27-05-2015.
 */
public class MicrolocationListResponseProcessor implements Callback<MicrolocationResponseList> {
    private static final String TAG = "Microprocessor";
    ArrayList<String> queries = new ArrayList<String>();

    @Override
    public void success(MicrolocationResponseList microlocationResponseList, Response response) {
        for (Microlocation microlocation : microlocationResponseList.microlocations)

        {
            String query = microlocation.generateSql();
            queries.add(query);
            Log.d(TAG, query);
        }
        DbSingleton dbSingleton = DbSingleton.getInstance();

        dbSingleton.clearDatabase(DbContract.Microlocation.TABLE_NAME);
        dbSingleton.insertQueries(queries);

        Bus bus = OpenEventApp.getEventBus();
        bus.post(new MicrolocationDownloadEvent(true));
    }

    @Override
    public void failure(RetrofitError error) {
        Bus bus = OpenEventApp.getEventBus();
        bus.post(new MicrolocationDownloadEvent(false));
    }
}
