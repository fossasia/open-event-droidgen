package org.fossasia.openevent.api.processor;

import android.util.Log;

import org.fossasia.openevent.api.protocol.SpeakerResponseList;
import org.fossasia.openevent.data.SessionSpeakersMapping;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.Progress;

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
    public void success(SpeakerResponseList speakerResponseList, Response response) {
        ArrayList<String> queries = new ArrayList<String>();

        for (Speaker speaker : speakerResponseList.speakers) {
            for (int i = 0; i < speaker.getSession().length; i++) {
                SessionSpeakersMapping sessionSpeakersMapping = new SessionSpeakersMapping(speaker.getSession()[i], speaker.getId());
                sessionSpeakersMapping.generateSql();
            }
            String query = speaker.generateSql();
            queries.add(query);
            Log.d(TAG, query);
        }

        DbSingleton dbSingleton = DbSingleton.getInstance();
        dbSingleton.clearDatabase(DbContract.Speakers.TABLE_NAME);
        dbSingleton.insertQueries(queries);

        Progress progress1 = new Progress();
        Log.d(TAG, progress1.progress + " BEF");
        progress1.increaseProgress();
        Log.d(TAG, progress1.progress + " AF");
    }

    @Override
    public void failure(RetrofitError error) {
        // Do something with failure, raise an event etc.

    }
}
