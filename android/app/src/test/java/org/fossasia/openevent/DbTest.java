package org.fossasia.openevent;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;

import org.fossasia.openevent.dbutils.DbContract;
import org.fossasia.openevent.dbutils.DbHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: opticod(Anupam Das)
 * Date: 5/4/16
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)

public class DbTest {
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
    public void testDbVer() throws Exception {
        assertTrue(database.isOpen());
        assertEquals(DbContract.DATABASE_VERSION, database.getVersion());
    }

    @Test
    public void testDbName() throws Exception {
        assertEquals(DbContract.DATABASE_NAME, db.getDatabaseName());
    }

    @After
    public void tearDown() throws Exception {
        mActivity.deleteDatabase(DbContract.DATABASE_NAME);
        db.close();
    }
}

