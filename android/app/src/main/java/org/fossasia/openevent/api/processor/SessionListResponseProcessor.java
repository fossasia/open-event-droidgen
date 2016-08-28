package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.data.ServerSessionIdMappings;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.SessionDownloadEvent;
import org.fossasia.openevent.utils.CommonTaskLoop;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


/**
 * User: MananWason
 * Date: 27-05-2015
 */
public class SessionListResponseProcessor implements Callback<List<Session>> {

    @Override
    public void onResponse(Call<List<Session>> call, final Response<List<Session>> response) {
        if (response.isSuccessful()) {
            CommonTaskLoop.getInstance().post(new Runnable() {

                @Override
                public void run() {
                    DbSingleton dbSingleton = DbSingleton.getInstance();
                    ArrayList<String> queries = new ArrayList<String>();
                    for (int i = 0; i < response.body().size(); i++) {
                        Session session = response.body().get(i);
                        session.setStartDate(session.getStartTime().split("T")[0]);
                        ServerSessionIdMappings mapping = new ServerSessionIdMappings(session.getId(), i);
                        queries.add(mapping.generateSql());
                        session.setId(i);
                        String query = session.generateSql();
                        queries.add(query);
                        Timber.d(query);
                    }

                    dbSingleton.clearTable(DbContract.Sessions.TABLE_NAME);
                    dbSingleton.insertQueries(queries);
                    OpenEventApp.postEventOnUIThread(new SessionDownloadEvent(true));
                }
            });
        } else {
            OpenEventApp.getEventBus().post(new SessionDownloadEvent(false));
        }
    }

    @Override
    public void onFailure(Call<List<Session>> call, Throwable t) {
        OpenEventApp.getEventBus().post(new SessionDownloadEvent(false));
    }
}