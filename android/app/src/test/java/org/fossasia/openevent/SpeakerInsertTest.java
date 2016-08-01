package org.fossasia.openevent;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.fossasia.openevent.api.Urls;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbHelper;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.readJson;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Type;
import java.util.List;

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
    public void testSpeakerDbInsertion() throws JSONException {


        Gson gson = new Gson();
        Type listType = new TypeToken<List<Speaker>>() {
        }.getType();
        List<Speaker> speakers = gson.fromJson(readJson.readJsonAsset(Urls.SPEAKERS, mActivity), listType);

        if (speakers.size() > 0) {

            Speaker speaker = speakers.get(0);
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
    }

    @After
    public void tearDown() throws Exception {
        mActivity.deleteDatabase(DbContract.DATABASE_NAME);
        db.close();
    }


}

