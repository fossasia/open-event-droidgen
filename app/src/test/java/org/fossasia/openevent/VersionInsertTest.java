package org.fossasia.openevent;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.google.gson.Gson;

import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.data.Version;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * User: opticod(Anupam Das)
 * Date: 13/4/16
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)

public class VersionInsertTest {
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
    public void testTrackDbInsertionHttp() throws JSONException {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonStr = null;

        try {
            final String BASE_URL = Urls.BASE_GET_URL_ALT + Urls.EVENTS + "/" + Urls.EVENT_ID + "/" + Urls.VERSION;

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
            JSONArray eventJsonArray = json.getJSONArray(Urls.VERSION);
            if (eventJsonArray.length() > 0) {

                JSONObject eventJsonObject = eventJsonArray.getJSONObject(0);
                Version version = gson.fromJson(String.valueOf(eventJsonObject), Version.class);

                String query = version.generateSql();
                DbSingleton instance = new DbSingleton(database, mActivity, db);
                instance.clearDatabase(DbContract.Versions.TABLE_NAME);
                instance.insertQuery(query);

                Version versionDetails = instance.getVersionIds();
                assertNotNull(versionDetails);
                assertEquals(version.getId(), versionDetails.getId());
                assertEquals(version.getEventVer(), versionDetails.getEventVer());
                assertEquals(version.getMicrolocationsVer(), versionDetails.getMicrolocationsVer());
                assertEquals(version.getTracksVer(), versionDetails.getTracksVer());
                assertEquals(version.getSponsorVer(), versionDetails.getSponsorVer());
                assertEquals(version.getSpeakerVer(), versionDetails.getSpeakerVer());
                assertEquals(version.getSessionVer(), versionDetails.getSessionVer());
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

