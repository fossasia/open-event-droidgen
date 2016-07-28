package org.fossasia.openevent;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.data.Event;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbHelper;
import org.fossasia.openevent.dbutils.DbSingleton;
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
import static org.junit.Assert.assertNotNull;

/**
 * User: opticod(Anupam Das)
 * Date: 5/4/16
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)

public class EventInsertTest {

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
    public void testEventDbInsertionHttp() throws JSONException {

        Gson gson = new Gson();
        try {
            JSONObject json = new JSONObject(Urls.EVENT);

            Event event = gson.fromJson(String.valueOf(json), Event.class);

            String query = event.generateSql();
            DbSingleton instance = new DbSingleton(mActivity);
            instance.clearTable(DbContract.Event.TABLE_NAME);
            instance.insertQuery(query);

            Event eventDetails = instance.getEventDetails();
            assertNotNull(eventDetails);
            assertEquals(event.getEmail(), eventDetails.getEmail());
            assertEquals(event.getLogo(), eventDetails.getLogo());
            assertEquals(event.getStart(), eventDetails.getStart());
            assertEquals(event.getEnd(), eventDetails.getEnd());
            assertEquals(event.getLocationName(), eventDetails.getLocationName());
            assertEquals(event.getUrl(), eventDetails.getUrl());
            assertEquals(event.getId(), eventDetails.getId());
            assertEquals(event.getLatitude(), eventDetails.getLatitude(), 0.001);
            assertEquals(event.getLongitude(), eventDetails.getLongitude(), 0.001);

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
