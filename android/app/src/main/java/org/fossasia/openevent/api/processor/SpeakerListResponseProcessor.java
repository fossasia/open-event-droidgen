package org.fossasia.openevent.api.processor;

import android.util.Log;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.data.SessionSpeakersMapping;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.SpeakerDownloadEvent;
import org.fossasia.openevent.utils.CommonTaskLoop;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * User: mohit
 * Date: 25/5/15
 */
public class SpeakerListResponseProcessor implements Callback<List<Speaker>> {
    private final String TAG = "Speaker";

    @Override
    public void onResponse(Call<List<Speaker>> call, final Response<List<Speaker>> response) {
        if (response.isSuccessful()) {
            CommonTaskLoop.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    ArrayList<String> queries = new ArrayList<String>();

                    for (Speaker speaker : response.body()) {
                        for (int i = 0; i < speaker.getSessions().size(); i++) {
                            SessionSpeakersMapping sessionSpeakersMapping = new SessionSpeakersMapping(speaker.getSessions().get(i).getId(), speaker.getId());
                            String query_ss = sessionSpeakersMapping.generateSql();
                            queries.add(query_ss);
                        }
                        String query = speaker.generateSql();
                        queries.add(query);
                        Log.d(TAG, query);
                    }

                    DbSingleton dbSingleton = DbSingleton.getInstance();
                    dbSingleton.clearDatabase(DbContract.Sessionsspeakers.TABLE_NAME);
                    dbSingleton.clearDatabase(DbContract.Speakers.TABLE_NAME);
                    dbSingleton.insertQueries(queries);

                    OpenEventApp.postEventOnUIThread(new SpeakerDownloadEvent(true));
                }
            });
        } else {
            OpenEventApp.getEventBus().post(new SpeakerDownloadEvent(false));
        }
    }

    @Override
    public void onFailure(Call<List<Speaker>> call, Throwable t) {
        OpenEventApp.getEventBus().post(new SpeakerDownloadEvent(false));
    }
}
