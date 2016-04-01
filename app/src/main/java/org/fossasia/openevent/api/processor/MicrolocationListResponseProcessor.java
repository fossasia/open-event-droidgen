package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.api.protocol.MicrolocationResponseList;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.MicrolocationDownloadEvent;
import org.fossasia.openevent.utils.CommonTaskLoop;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by MananWason on 27-05-2015.
 */
public class MicrolocationListResponseProcessor implements Callback<MicrolocationResponseList> {
    private static final String TAG = "Microprocessor";

    ArrayList<String> queries = new ArrayList<String>();

    @Override
    public void success(final MicrolocationResponseList microlocationResponseList, Response response) {
        CommonTaskLoop.getInstance().post(new Runnable() {
            @Override
            public void run() {
                for (Microlocation microlocation : microlocationResponseList.microlocations)
                {
                    String query = microlocation.generateSql();
                    queries.add(query);
                    Timber.tag(TAG).d(query);
                }
                DbSingleton dbSingleton = DbSingleton.getInstance();

                dbSingleton.clearDatabase(DbContract.Microlocation.TABLE_NAME);
                dbSingleton.insertQueries(queries);

                OpenEventApp.postEventOnUIThread(new MicrolocationDownloadEvent(true));
            }
        });

    }

    @Override
    public void failure(RetrofitError error) {
        OpenEventApp.getEventBus().post(new MicrolocationDownloadEvent(false));
    }
}
