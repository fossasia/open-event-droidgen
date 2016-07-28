package org.fossasia.openevent;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

import org.fossasia.openevent.api.Urls;
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

import java.io.IOException;
import java.io.InputStream;

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


        Gson gson = new Gson();
        try {
            JSONObject json = new JSONObject(readJsonAsset(Urls.SPEAKERS));
            JSONArray speakerJsonArray = json.getJSONArray(Urls.SPEAKERS);
            if (speakerJsonArray.length() > 0) {

                JSONObject eventJsonObject = speakerJsonArray.getJSONObject(0);
                Speaker speaker = gson.fromJson(String.valueOf(eventJsonObject), Speaker.class);

                String query = speaker.generateSql();
                DbSingleton instance = new DbSingleton(database, mActivity, db);
                instance.clearTable(DbContract.Speakers.TABLE_NAME);
                instance.insertQuery(query);

                Speaker speakerDetails = instance.getSpeakerList(DbContract.Speakers.ID).get(0);
                assertEquals(speaker.getName(), speakerDetails.getName());
                assertEquals(speaker.getName(), speakerDetails.getName());
                assertEquals(speaker.getBio(), speakerDetails.getBio());
                assertEquals(speaker.getCountry(), speakerDetails.getCountry());
                assertEquals(speaker.getEmail(), speakerDetails.getEmail());
                assertEquals(speaker.getPhoto(), speakerDetails.getPhoto());
                assertEquals(speaker.getWebsite(), speakerDetails.getWebsite());
            }
        } catch (JSONException e) {
        }
    }

    @After
    public void tearDown() throws Exception {
        mActivity.deleteDatabase(DbContract.DATABASE_NAME);
        db.close();
    }

    public String readJsonAsset(final String name) {
        String json = null;

        try {
            InputStream inputStream = mActivity.getAssets().open(name + ".json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");


        } catch (IOException e) {
            e.printStackTrace();


        }

        return json;
    }

}

