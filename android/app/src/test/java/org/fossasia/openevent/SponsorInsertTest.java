package org.fossasia.openevent;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.fossasia.openevent.data.Sponsor;
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

public class SponsorInsertTest {

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
    public void testSponsorDbInsertion() throws JSONException {

        Gson gson = new Gson();
        Type listType = new TypeToken<List<Sponsor>>() {
        }.getType();
        List<Sponsor> sponsors = gson.fromJson(readJson.readJsonAsset(ConstantStrings.Sponsors, mActivity), listType);
        if (sponsors.size() > 0) {

            Sponsor sponsor = sponsors.get(0);

            String query = sponsor.generateSql();
            DbSingleton instance = new DbSingleton(mActivity);
            instance.clearTable(DbContract.Sponsors.TABLE_NAME);
            instance.insertQuery(query);

            Sponsor firstSponsor = instance.getSponsorList().get(0);
            assertNotNull(firstSponsor);
            assertEquals(sponsor.getId(), firstSponsor.getId());
            assertEquals(sponsor.getName(), firstSponsor.getName());
            assertEquals(sponsor.getUrl(), firstSponsor.getUrl());
        }
    }

    @After
    public void tearDown() throws Exception {
        mActivity.deleteDatabase(DbContract.DATABASE_NAME);
        db.close();
    }
}
