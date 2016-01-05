package org.fossasia.openevent.api.processor;

import android.util.Log;

import org.fossasia.openevent.OpenEventApp;
import org.fossasia.openevent.api.protocol.SpeakerResponseList;
import org.fossasia.openevent.data.SessionSpeakersMapping;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.events.SpeakerDownloadEvent;
import org.fossasia.openevent.events.SponsorDownloadEvent;
import org.fossasia.openevent.utils.CommonTaskLoop;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * User: mohit
 * Date: 25/5/15
 */
public class SpeakerListResponseProcessor implements Callback<SpeakerResponseList> {
    private final String TAG = "Speaker";

    @Override
    public void success(final SpeakerResponseList speakerResponseList, Response response) {
        CommonTaskLoop.getInstance().post(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> queries = new ArrayList<String>();

                for (Speaker speaker : speakerResponseList.speakers) {
                    for (int i = 0; i < speaker.getSession().length; i++) {
                        SessionSpeakersMapping sessionSpeakersMapping = new SessionSpeakersMapping(speaker.getSession()[i], speaker.getId());
                        String query_ss = sessionSpeakersMapping.generateSql();
                        queries.add(query_ss);
                    }
                    String query = speaker.generateSql();
                    queries.add(query);
                    Log.d(TAG, query);
                }

                DbSingleton dbSingleton = DbSingleton.getInstance();
                dbSingleton.clearDatabase(DbContract.Speakers.TABLE_NAME);
                dbSingleton.insertQueries(queries);

                OpenEventApp.postEventOnUIThread(new SpeakerDownloadEvent(true));
            }
        });


    }

    @Override
    public void failure(RetrofitError error) {
        // Do something with failure, raise an event etc.
        OpenEventApp.getEventBus().post(new SpeakerDownloadEvent(false));
    }
}
