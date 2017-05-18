package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.data.Microlocation;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.MicrolocationDownloadEvent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by MananWason on 27-05-2015.
 */
public class MicrolocationListResponseProcessor implements Callback<List<Microlocation>> {

    private ArrayList<String> queries = new ArrayList<>();

    @Override
    public void onResponse(Call<List<Microlocation>> call, final Response<List<Microlocation>> response) {
        if (response.isSuccessful()) {
            Completable.fromAction(() -> {
                for (Microlocation microlocation : response.body()) {
                    String query = microlocation.generateSql();
                    queries.add(query);
                    Timber.d(query);
                }
                DbSingleton dbSingleton = DbSingleton.getInstance();

                dbSingleton.clearTable(DbContract.Microlocation.TABLE_NAME);
                dbSingleton.insertQueries(queries);

                OpenEventApp.postEventOnUIThread(new MicrolocationDownloadEvent(true));
            }).subscribeOn(Schedulers.computation()).subscribe();
        } else {
            OpenEventApp.getEventBus().post(new MicrolocationDownloadEvent(false));
        }
    }

    @Override
    public void onFailure(Call<List<Microlocation>> call, Throwable t) {
        OpenEventApp.getEventBus().post(new MicrolocationDownloadEvent(false));
    }
}