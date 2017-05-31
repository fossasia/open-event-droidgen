package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.data.SessionSpeakersMapping;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.SpeakerDownloadEvent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


/**
 * User: mohit
 * Date: 25/5/15
 */
public class SpeakerListResponseProcessor implements Callback<List<Speaker>> {

    @Override
    public void onResponse(Call<List<Speaker>> call, final Response<List<Speaker>> response) {
        if (response.isSuccessful()) {
            Completable.fromAction(() -> {
                ArrayList<String> queries = new ArrayList<>();

                for (Speaker speaker : response.body()) {
                    if (speaker.getSession() != null) {
                        for (int i = 0; i < speaker.getSession().size(); i++) {
                            SessionSpeakersMapping sessionSpeakersMapping = new SessionSpeakersMapping(speaker.getSession().get(i).getId(), speaker.getId());
                            String query_ss = sessionSpeakersMapping.generateSql();
                            queries.add(query_ss);
                        }
                    }
                    String query = speaker.generateSql();
                    queries.add(query);
                    Timber.d(query);
                }

                DbSingleton dbSingleton = DbSingleton.getInstance();
                dbSingleton.clearTable(DbContract.Sessionsspeakers.TABLE_NAME);
                dbSingleton.clearTable(DbContract.Speakers.TABLE_NAME);
                dbSingleton.insertQueries(queries);

                OpenEventApp.postEventOnUIThread(new SpeakerDownloadEvent(true));
            }).subscribeOn(Schedulers.computation()).subscribe();
        } else {
            OpenEventApp.getEventBus().post(new SpeakerDownloadEvent(false));
        }
    }

    @Override
    public void onFailure(Call<List<Speaker>> call, Throwable t) {
        OpenEventApp.getEventBus().post(new SpeakerDownloadEvent(false));
    }
}