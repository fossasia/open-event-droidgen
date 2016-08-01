package org.fossasia.openevent;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.fossasia.openevent.data.Track;
import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbHelper;
import org.fossasia.openevent.dbutils.DbSingleton;
import org.fossasia.openevent.utils.ConstantStrings;
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
import static org.junit.Assert.assertNotNull;


/**
 * User: opticod(Anupam Das)
 * Date: 5/4/16
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)

public class TracksInsertTest {
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
    public void testTrackDbInsertion() throws JSONException {

        Gson gson = new Gson();

        Type listType = new TypeToken<List<Track>>(){}.getType();
        List<Track> tracks = gson.fromJson(readJson.readJsonAsset(ConstantStrings.Tracks, mActivity), listType);
        if (tracks.size() > 0) {

            Track track = tracks.get(0);

            String query = track.generateSql();
            DbSingleton instance = new DbSingleton(database, mActivity, db);
            instance.clearTable(DbContract.Tracks.TABLE_NAME);
            instance.insertQuery(query);

            Track trackDetails = instance.getTrackbyId(track.getId());
            assertNotNull(trackDetails);
            assertEquals(track.getDescription(), trackDetails.getDescription());
            assertEquals(track.getName(), trackDetails.getName());
            assertEquals(track.getImage(), trackDetails.getImage());
        }
    }

    @After
    public void tearDown() throws Exception {
        mActivity.deleteDatabase(DbContract.DATABASE_NAME);
        db.close();
    }
}

