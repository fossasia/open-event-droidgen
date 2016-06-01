package org.fossasia.openevent;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.google.gson.Gson;

import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.data.SessionSpeakersMapping;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbHelper;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;


/**
 * User: opticod(Anupam Das)
 * Date: 5/4/16
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)

public class SpeakerInsertTest {
    private Activity mActivity;

    private SQLiteDatabase database;

    private DbHelper db;

    @Before
    public void setUp() throws Exception {
        mActivity = Robolectric.setupActivity(Activity.class);
        db = new DbHelper(mActivity);
        database = db.getWritableDatabase();
    }

    @Test
    public void testSpeakerDbInsertionHttp() throws JSONException {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonStr = null;

        try {
            final String BASE_URL = Urls.BASE_GET_URL_ALT + Urls.EVENTS + "/" + Urls.EVENT_ID + "/" + Urls.SPEAKERS;

            Uri builtUri = Uri.parse(BASE_URL).buildUpon().build();
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return;
            }
            jsonStr = buffer.toString();
        } catch (IOException e) {
            return;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                }
            }
        }
        Gson gson = new Gson();
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray eventJsonArray = json.getJSONArray(Urls.SPEAKERS);
            if (eventJsonArray.length() > 0) {

                JSONObject eventJsonObject = eventJsonArray.getJSONObject(0);
                Speaker speaker = gson.fromJson(String.valueOf(eventJsonObject), Speaker.class);
                JSONArray sessionJsonArray = eventJsonObject.getJSONArray(Urls.SESSIONS);

                String query = speaker.generateSql();
                DbSingleton instance = new DbSingleton(database, mActivity, db);
                instance.clearDatabase(DbContract.Speakers.TABLE_NAME);
                instance.clearDatabase(DbContract.Sessionsspeakers.TABLE_NAME);
                instance.insertQuery(query);
                Speaker speakerDetails = instance.getSpeakerList(DbContract.Speakers.ID).get(0);
                assertEquals(speaker.getId(), speakerDetails.getId());
                assertEquals(speaker.getTwitter(), speakerDetails.getTwitter());
                assertEquals(speaker.getFacebook(), speakerDetails.getFacebook());
                assertEquals(speaker.getGithub(), speakerDetails.getGithub());
                assertEquals(speaker.getLinkedin(), speakerDetails.getLinkedin());
                assertEquals(speaker.getOrganisation(), speakerDetails.getOrganisation());
                assertEquals(speaker.getPosition(), speakerDetails.getPosition());
                assertEquals(speaker.getBio(), speakerDetails.getBio());
                if (sessionJsonArray != null) {
                    int sessions[] = new int[sessionJsonArray.length()];
                    for (int i = 0; i < sessionJsonArray.length(); ++i) {
                        sessions[i] = sessionJsonArray.optInt(i);
                    }
                    for (int i = 0; i < speaker.getSession().length; i++) {
                        //will also test sessionspeakers table insertion
                        SessionSpeakersMapping sessionSpeakersMapping = new SessionSpeakersMapping(sessions[i], speaker.getId());
                        String query_ss = sessionSpeakersMapping.generateSql();
                        instance.insertQuery(query_ss);
                    }
                    ArrayList<Integer> sessionsIds = instance.getSessionIdsbySpeakerId(speaker.getId());
                    for (int i = 0; i < speaker.getSession().length; i++) {
                        assertEquals(Integer.valueOf(sessions[i]), sessionsIds.get(i));
                    }
                }
                assertEquals(speaker.getCountry(), speakerDetails.getCountry());
                assertEquals(speaker.getEmail(), speakerDetails.getEmail());
                assertEquals(speaker.getPhoto(), speakerDetails.getPhoto());
                assertEquals(speaker.getWeb(), speakerDetails.getWeb());
            }
        } catch (JSONException e) {
        }
    }

    @After
    public void tearDown() throws Exception {
        mActivity.deleteDatabase(DbContract.DATABASE_NAME);
        db.close();
    }
}

