package org.fossasia.openevent.api.processor;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.api.protocol.SpeakerResponseList;
import org.fossasia.openevent.data.SessionSpeakersMapping;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.SpeakerDownloadEvent;
import org.fossasia.openevent.utils.CommonTaskLoop;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


/**
 * User: mohit
 * Date: 25/5/15
 */
public class SpeakerListResponseProcessor implements Callback<SpeakerResponseList> {

    @Override
    public void onResponse(Call<SpeakerResponseList> call, final Response<SpeakerResponseList> response) {
        if (response.isSuccessful()) {
            CommonTaskLoop.getInstance().post(new Runnable() {
                @Override
                public void run() {
                    ArrayList<String> queries = new ArrayList<>();

                    for (Speaker speaker : response.body().speakers) {
                        for (int i = 0; i < speaker.getSession().length; i++) {
                            SessionSpeakersMapping sessionSpeakersMapping = new SessionSpeakersMapping(speaker.getSession()[i], speaker.getId());
                            String query_ss = sessionSpeakersMapping.generateSql();
                            queries.add(query_ss);
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
                }
            });
        } else {
            OpenEventApp.getEventBus().post(new SpeakerDownloadEvent(false));
        }
    }

    @Override
    public void onFailure(Call<SpeakerResponseList> call, Throwable t) {
        OpenEventApp.getEventBus().post(new SpeakerDownloadEvent(false));
    }
}
