package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.data.Session;
import org.fossasia.openevent.data.parsingExtra.Microlocation;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.SessionDownloadEvent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
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
            Completable.fromAction(() -> {
                DbSingleton dbSingleton = DbSingleton.getInstance();
                ArrayList<String> queries = new ArrayList<String>();
                for (int i = 0; i < response.body().size(); i++) {
                    Session session = response.body().get(i);
                    if(session.getMicrolocation() == null){
                        session.setMicrolocation(new Microlocation(0,""));
                    }
                    session.setStartDate(session.getStartTime().split("T")[0]);
                    String query = session.generateSql();
                    queries.add(query);
                    Timber.d(query);
                }

                dbSingleton.clearTable(DbContract.Sessions.TABLE_NAME);
                dbSingleton.insertQueries(queries);
                OpenEventApp.postEventOnUIThread(new SessionDownloadEvent(true));
            }).subscribeOn(Schedulers.computation()).subscribe();
        } else {
            OpenEventApp.getEventBus().post(new SessionDownloadEvent(false));
        }
    }

    @Override
    public void onFailure(Call<List<Session>> call, Throwable t) {
        OpenEventApp.getEventBus().post(new SessionDownloadEvent(false));
    }
}